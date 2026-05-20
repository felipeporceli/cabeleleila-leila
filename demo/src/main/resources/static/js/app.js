'use strict';

// ============================================================
// AUTH
// ============================================================
const auth = {
  get user() { return sessionStorage.getItem('_u') || ''; },
  get pass() { return sessionStorage.getItem('_p') || ''; },
  get header() {
    return 'Basic ' + btoa(unescape(encodeURIComponent(this.user + ':' + this.pass)));
  },
  save(u, p) { sessionStorage.setItem('_u', u); sessionStorage.setItem('_p', p); },
  clear() {
    sessionStorage.removeItem('_u');
    sessionStorage.removeItem('_p');
    sessionStorage.removeItem('_cid');
  },
  isLoggedIn() { return !!this.user; }
};

// ============================================================
// ROLE
// ============================================================
var isAdmin = true;
var myClienteId = null;

function applyRole() {
  isAdmin = (auth.user === 'leila');
  document.body.classList.toggle('role-admin',   isAdmin);
  document.body.classList.toggle('role-cliente', !isAdmin);
}

// ============================================================
// API HELPER
// ============================================================
async function apiCall(method, path, body) {
  if (body === undefined) body = null;
  var opts = {
    method: method,
    headers: {
      'Content-Type': 'application/json',
      'Authorization': auth.header
    }
  };
  if (body !== null) opts.body = JSON.stringify(body);

  try {
    var res = await fetch(path, opts);

    if (res.status === 401) {
      auth.clear();
      renderLogin();
      return null;
    }

    if (res.status === 204) return {};

    var data;
    try { data = await res.json(); } catch(e) { data = {}; }

    if (!res.ok) { showApiError(data); return null; }

    return data;
  } catch (err) {
    showToast('Erro de conexão com o servidor.', 'error');
    return null;
  }
}

// ============================================================
// TOAST & CONFIRM
// ============================================================
function showToast(msg, type) {
  if (type === undefined) type = 'success';
  var container = document.getElementById('toast-container');
  if (!container) {
    container = document.createElement('div');
    container.id = 'toast-container';
    document.body.appendChild(container);
  }
  var icons = { success: '✓', error: '✕', warning: '!', info: 'i' };
  var safe = String(msg)
    .replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;')
    .replace(/\n/g,'<br>');
  var toast = document.createElement('div');
  toast.className = 'toast toast-' + type;
  toast.innerHTML =
    '<span class="toast-icon">' + (icons[type] || '✓') + '</span>' +
    '<span class="toast-msg">' + safe + '</span>' +
    '<button class="toast-close" onclick="this.parentElement.remove()">&#x2715;</button>';
  container.appendChild(toast);
  requestAnimationFrame(function() { toast.classList.add('toast-show'); });
  setTimeout(function() {
    toast.classList.add('toast-hide');
    setTimeout(function() { if (toast.parentElement) toast.remove(); }, 400);
  }, 4500);
}

function showConfirm(msg) {
  return new Promise(function(resolve) {
    var overlay = document.createElement('div');
    overlay.className = 'confirm-overlay';
    var safe = String(msg).replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;');
    overlay.innerHTML =
      '<div class="confirm-card">' +
        '<p class="confirm-msg">' + safe + '</p>' +
        '<div class="confirm-btns">' +
          '<button class="confirm-cancel-btn">Cancelar</button>' +
          '<button class="confirm-ok-btn">Confirmar</button>' +
        '</div>' +
      '</div>';
    function done(val) { overlay.remove(); resolve(val); }
    overlay.querySelector('.confirm-cancel-btn').addEventListener('click', function() { done(false); });
    overlay.querySelector('.confirm-ok-btn').addEventListener('click', function() { done(true); });
    overlay.addEventListener('click', function(e) { if (e.target === overlay) done(false); });
    document.body.appendChild(overlay);
    requestAnimationFrame(function() { overlay.classList.add('confirm-show'); });
  });
}

function showApiError(data) {
  if (!data) { showToast('Erro desconhecido.', 'error'); return; }
  var msg = data.mensagem || ('Erro ' + (data.status || ''));
  if (data.erros && data.erros.length) {
    msg += '\n' + data.erros.map(function(e) { return '• ' + e.campo + ': ' + e.erro; }).join('\n');
  }
  showToast(msg, 'error');
}

// ============================================================
// DATE / TIME HELPERS
// Text inputs with masks produce dd/MM/yyyy HH:mm or dd/MM/yyyy
// directly — no conversion needed when reading form values.
// ============================================================
function htmlDTtoApi(v) {
  return v || null;
}

function htmlDateToApi(v) {
  return v || null;
}

function apiDTtoHtml(v) {
  // API already returns dd/MM/yyyy HH:mm — use as-is for text inputs
  return v ? v.substring(0, 16) : '';
}

// ============================================================
// DATE INPUT MASKS
// ============================================================
function applyDatetimeMask(el) {
  el.addEventListener('input', function() {
    var v = this.value.replace(/\D/g, '').substring(0, 12);
    var out = '';
    for (var i = 0; i < v.length; i++) {
      if (i === 2 || i === 4) out += '/';
      else if (i === 8) out += ' ';
      else if (i === 10) out += ':';
      out += v[i];
    }
    this.value = out;
  });
}

function applyDateMask(el) {
  el.addEventListener('input', function() {
    var v = this.value.replace(/\D/g, '').substring(0, 8);
    var out = '';
    for (var i = 0; i < v.length; i++) {
      if (i === 2 || i === 4) out += '/';
      out += v[i];
    }
    this.value = out;
  });
}

function initDateMasks() {
  document.querySelectorAll('.input-datetime').forEach(applyDatetimeMask);
  document.querySelectorAll('.input-date').forEach(applyDateMask);
}

// ============================================================
// DATETIME SPLIT HELPERS (date text + time select)
// ============================================================
function buildTimeOptions() {
  var opts = '<option value="">Hora...</option>';
  for (var h = 0; h < 24; h++) {
    for (var m = 0; m < 60; m += 30) {
      var t = String(h).padStart(2, '0') + ':' + String(m).padStart(2, '0');
      opts += '<option value="' + t + '">' + t + '</option>';
    }
  }
  return opts;
}

function initTimeSelects() {
  var opts = buildTimeOptions();
  document.querySelectorAll('.time-select').forEach(function(sel) { sel.innerHTML = opts; });
}

function getDateTimeValue(form, baseName) {
  var dtEl = form[baseName + 'Dt'];
  var hrEl = form[baseName + 'Hr'];
  var dt = dtEl ? dtEl.value : '';
  var hr = hrEl ? hrEl.value : '';
  if (!dt && !hr) return null;
  return dt + (hr ? ' ' + hr : '');
}

function setDateTimeValue(form, baseName, apiValue) {
  var dtEl = form[baseName + 'Dt'];
  var hrEl = form[baseName + 'Hr'];
  if (!apiValue) {
    if (dtEl) dtEl.value = '';
    if (hrEl) hrEl.value = '';
    return;
  }
  var parts = apiValue.split(' ');
  if (dtEl) dtEl.value = parts[0] || '';
  if (hrEl) hrEl.value = parts[1] ? parts[1].substring(0, 5) : '';
}

function apiTimeToHtml(v) {
  if (!v) return '';
  if (Array.isArray(v)) {
    return String(v[0]).padStart(2, '0') + ':' + String(v[1] || 0).padStart(2, '0');
  }
  return String(v).substring(0, 5);
}

function htmlTimeToApi(v) {
  if (!v) return null;
  return v.length === 5 ? v + ':00' : v;
}

// ============================================================
// UTILITIES
// ============================================================
function $(id) { return document.getElementById(id); }

function buildQuery(obj) {
  var p = new URLSearchParams();
  for (var k in obj) {
    var v = obj[k];
    if (v !== null && v !== undefined && v !== '') p.set(k, String(v));
  }
  return p.toString();
}

function statusBadge(status) {
  var map = {
    PENDENTE: 'badge-pendente',
    CONFIRMADO: 'badge-confirmado',
    CANCELADO: 'badge-cancelado',
    CONCLUIDO: 'badge-concluido',
    PAGO: 'badge-pago'
  };
  var cls = map[status] || 'badge-default';
  return '<span class="badge ' + cls + '">' + (status || '-') + '</span>';
}

function emptyRow(cols, msg) {
  msg = msg || 'Nenhum registro encontrado';
  return '<tr><td colspan="' + cols + '" class="empty-msg">' + msg + '</td></tr>';
}

function renderPagination(containerId, currentPage, totalPages, fnName) {
  var el = $(containerId);
  if (!el) return;
  if (!totalPages || totalPages <= 1) { el.innerHTML = ''; return; }
  el.innerHTML =
    '<button onclick="' + fnName + '(' + (currentPage - 1) + ')"' +
    (currentPage === 0 ? ' disabled' : '') + '>&larr; Anterior</button>' +
    '<span>P&aacute;gina ' + (currentPage + 1) + ' de ' + totalPages + '</span>' +
    '<button onclick="' + fnName + '(' + (currentPage + 1) + ')"' +
    (currentPage >= totalPages - 1 ? ' disabled' : '') + '>Pr&oacute;xima &rarr;</button>';
}

function esc(s) {
  if (s == null) return '';
  return String(s)
    .replace(/&/g, '&amp;').replace(/</g, '&lt;')
    .replace(/>/g, '&gt;').replace(/"/g, '&quot;');
}

// ============================================================
// SCREEN CONTROL
// ============================================================
function renderLogin() {
  $('login-screen').style.display = 'flex';
  $('app-screen').style.display = 'none';
}

function renderApp() {
  $('login-screen').style.display = 'none';
  $('app-screen').style.display = 'block';
  $('logged-user').textContent = auth.user;
  loadAllCabeleireirosForSelects();
  loadAllClientesForSelect();
}

function showSection(name) {
  document.querySelectorAll('.app-section').forEach(function(s) { s.classList.remove('active'); });
  document.querySelectorAll('.nav-btn').forEach(function(b) { b.classList.remove('active'); });

  $(name + '-section').classList.add('active');
  $('nav-' + name).classList.add('active');

  var loaders = {
    clientes: loadClientes,
    cabeleireiros: loadCabeleireiros,
    agendamentos: function() {
      (isAdmin ? loadAgendamentos : loadMeusAgendamentos)();
      loadServicosParaSelecao();
    },
    horarios: loadHorarios,
    'horarios-cliente': loadCabeleireirosParaCliente,
    pagamentos: loadPagamentos,
    relatorio: function() {},
    servicos: loadServicos
  };
  if (loaders[name]) loaders[name]();
}

function logout() {
  auth.clear();
  isAdmin = true;
  myClienteId = null;
  document.body.classList.remove('role-admin', 'role-cliente');
  renderLogin();
}

// ============================================================
// LOGIN
// ============================================================
$('login-form').addEventListener('submit', async function(e) {
  e.preventDefault();
  var user = $('login-user').value.trim();
  var pass = $('login-pass').value;
  if (!user || !pass) return;

  auth.save(user, pass);
  var res = await apiCall('GET', '/cabeleireiros?tamanho-pagina=1&pagina=0');
  if (res === null) {
    auth.clear();
    showToast('Usuário ou senha incorretos.', 'error');
    return;
  }

  applyRole();
  if (!isAdmin) {
    myClienteId = 5;
    sessionStorage.setItem('_cid', myClienteId);
  } else {
    myClienteId = parseInt(sessionStorage.getItem('_cid')) || null;
  }
  renderApp();
  showSection(isAdmin ? 'clientes' : 'cabeleireiros');
});


// ============================================================
// CLIENTES — selects dinâmicos
// ============================================================
var allClientes = [];

async function loadAllClientesForSelect() {
  var data = await apiCall('GET', '/clientes?pagina=0&tamanho-pagina=200');
  if (!data) return;
  allClientes = data.content || [];
  populateClienteSelects();
}

function populateClienteSelects() {
  document.querySelectorAll('select[name="clienteId"]').forEach(function(sel) {
    if (!isAdmin) {
      // Cliente só vê o próprio nome, selecionado e bloqueado
      var meu = allClientes.find(function(c) { return c.id === myClienteId; });
      sel.innerHTML = meu
        ? '<option value="' + meu.id + '">' + esc(meu.nome) + '</option>'
        : '<option value="">Carregando...</option>';
      sel.value = myClienteId ? String(myClienteId) : '';
      sel.disabled = true;
    } else {
      var current = sel.value;
      var first = sel.required
        ? '<option value="">Selecione o cliente...</option>'
        : '<option value="">Todos</option>';
      sel.innerHTML = first + allClientes.map(function(c) {
        return '<option value="' + c.id + '">' + esc(c.nome) + '</option>';
      }).join('');
      sel.disabled = false;
      if (current) sel.value = current;
    }
  });
}

// Quando o cliente seleciona seu próprio nome no form de agendamento,
// atualiza myClienteId e recarrega "Meus Agendamentos"
$('agendamento-form').addEventListener('change', function(e) {
  if (!isAdmin && e.target.name === 'clienteId' && e.target.value) {
    myClienteId = parseInt(e.target.value);
    sessionStorage.setItem('_cid', myClienteId);
    loadMeusAgendamentos();
  }
});

// ============================================================
// CLIENTES (admin-only)
// ============================================================
var clientesData = { list: [], page: 0, filter: {} };
var editingClienteId = null;

async function loadClientes(page) {
  if (page === undefined) page = 0;
  clientesData.page = page;
  var data = await apiCall('GET', '/clientes?' + buildQuery(
    Object.assign({ pagina: page, 'tamanho-pagina': 10 }, clientesData.filter)
  ));
  if (!data) return;
  clientesData.list = data.content || [];
  renderClientesTable();
  renderPagination('clientes-pagination', data.number, data.totalPages, 'loadClientes');
}

function renderClientesTable() {
  var tb = $('clientes-tbody');
  if (!clientesData.list.length) { tb.innerHTML = emptyRow(7); return; }
  tb.innerHTML = clientesData.list.map(function(c) {
    return '<tr>' +
      '<td>' + c.id + '</td><td>' + esc(c.nome) + '</td><td>' + esc(c.cpf) + '</td>' +
      '<td>' + esc(c.email) + '</td><td>' + esc(c.telefone) + '</td><td>' + esc(c.cidade || '-') + '</td>' +
      '<td class="actions">' +
        '<button class="btn-edit" onclick="beginEditCliente(' + c.id + ')">Editar</button> ' +
        '<button class="btn-danger" onclick="deleteCliente(' + c.id + ')">Excluir</button>' +
      '</td></tr>';
  }).join('');
}

function beginEditCliente(id) {
  var c = clientesData.list.find(function(x) { return x.id === id; });
  if (!c) return;
  editingClienteId = id;
  $('cliente-form-title').textContent = 'Editar Cliente';
  var f = $('cliente-form');
  ['nome','cpf','email','telefone','cep','logradouro','numero','bairro','cidade','complemento']
    .forEach(function(k) { f[k].value = c[k] || ''; });
  f.senha.value = '';
  $('cliente-card').scrollIntoView({ behavior: 'smooth', block: 'start' });
}

function cancelEditCliente() {
  editingClienteId = null;
  $('cliente-form-title').textContent = 'Novo Cliente';
  $('cliente-form').reset();
}

$('cliente-form').addEventListener('submit', async function(e) {
  e.preventDefault();
  var f = e.target;
  var body = {
    nome: f.nome.value, cpf: f.cpf.value, email: f.email.value, telefone: f.telefone.value,
    cep: f.cep.value || null, logradouro: f.logradouro.value || null,
    numero: f.numero.value || null, bairro: f.bairro.value || null,
    cidade: f.cidade.value || null, complemento: f.complemento.value || null
  };
  if (f.senha.value) body.senha = f.senha.value;
  var res = editingClienteId
    ? await apiCall('PUT', '/clientes/' + editingClienteId, body)
    : await apiCall('POST', '/clientes', body);
  if (res !== null) {
    showToast(editingClienteId ? 'Cliente atualizado!' : 'Cliente cadastrado!');
    cancelEditCliente();
    loadClientes(clientesData.page);
    loadAllClientesForSelect();
  }
});

async function deleteCliente(id) {
  if (!await showConfirm('Excluir cliente #' + id + '?')) return;
  var res = await apiCall('DELETE', '/clientes/' + id);
  if (res !== null) {
    loadClientes(clientesData.page);
    loadAllClientesForSelect();
  }
}

$('cliente-filter-form').addEventListener('submit', function(e) {
  e.preventDefault();
  var f = e.target;
  clientesData.filter = { nome: f.nome.value, cpf: f.cpf.value, email: f.email.value, telefone: f.telefone.value, cep: f.cep.value };
  loadClientes(0);
});

function clearClienteFilter() {
  $('cliente-filter-form').reset();
  clientesData.filter = {};
  loadClientes(0);
}

// ============================================================
// CABELEIREIROS
// ============================================================
var cabeleireirosData = { list: [], page: 0, filter: {} };
var editingCabeleireiroId = null;

async function loadCabeleireiros(page) {
  if (page === undefined) page = 0;
  cabeleireirosData.page = page;
  var data = await apiCall('GET', '/cabeleireiros?' + buildQuery(
    Object.assign({ pagina: page, 'tamanho-pagina': 10 }, cabeleireirosData.filter)
  ));
  if (!data) return;
  cabeleireirosData.list = data.content || [];
  renderCabeleireirosTable();
  renderPagination('cabeleireiros-pagination', data.number, data.totalPages, 'loadCabeleireiros');
}

function renderCabeleireirosTable() {
  var tb = $('cabeleireiros-tbody');
  if (!cabeleireirosData.list.length) { tb.innerHTML = emptyRow(isAdmin ? 7 : 4); return; }
  tb.innerHTML = cabeleireirosData.list.map(function(c) {
    var acoes = isAdmin
      ? '<td class="actions">' +
          '<button class="btn-edit" onclick="beginEditCabeleireiro(' + c.id + ')">Editar</button> ' +
          '<button class="btn-danger" onclick="deleteCabeleireiro(' + c.id + ')">Excluir</button>' +
        '</td>'
      : '<td class="actions">' +
          '<button class="btn-confirm" onclick="irParaAgendar(' + c.id + ')">Agendar</button>' +
        '</td>';
    return '<tr>' +
      '<td>' + c.id + '</td>' +
      '<td>' + esc(c.nome) + '</td>' +
      '<td>' + esc(c.especialidade || '-') + '</td>' +
      '<td>' + esc(c.email) + '</td>' +
      '<td>' + esc(c.telefone || '-') + '</td>' +
      (isAdmin ? '<td class="admin-only">' + esc(c.cpf) + '</td>' : '') +
      acoes +
    '</tr>';
  }).join('');
}

// Atalho para o cliente: pré-preenche o cabeleireiroId e vai para agendamentos
function irParaAgendar(cabId) {
  showSection('agendamentos');
  var f = $('agendamento-form');
  if (f) f.cabeleireiroId.value = cabId;
}

function beginEditCabeleireiro(id) {
  var c = cabeleireirosData.list.find(function(x) { return x.id === id; });
  if (!c) return;
  editingCabeleireiroId = id;
  $('cabeleireiro-form-title').textContent = 'Editar Cabeleireiro';
  var f = $('cabeleireiro-form');
  ['nome','cpf','email','telefone'].forEach(function(k) { f[k].value = c[k] || ''; });
  f.especialidade.value = c.especialidade || '';
  f.senha.value = '';
  $('cabeleireiro-card').scrollIntoView({ behavior: 'smooth', block: 'start' });
}

function cancelEditCabeleireiro() {
  editingCabeleireiroId = null;
  $('cabeleireiro-form-title').textContent = 'Novo Cabeleireiro';
  $('cabeleireiro-form').reset();
}

$('cabeleireiro-form').addEventListener('submit', async function(e) {
  e.preventDefault();
  var f = e.target;
  var body = {
    nome: f.nome.value, cpf: f.cpf.value, email: f.email.value,
    telefone: f.telefone.value || null, especialidade: f.especialidade.value
  };
  if (f.senha.value) body.senha = f.senha.value;
  var res = editingCabeleireiroId
    ? await apiCall('PUT', '/cabeleireiros/' + editingCabeleireiroId, body)
    : await apiCall('POST', '/cabeleireiros', body);
  if (res !== null) {
    showToast(editingCabeleireiroId ? 'Cabeleireiro atualizado!' : 'Cabeleireiro cadastrado!');
    cancelEditCabeleireiro();
    loadCabeleireiros(cabeleireirosData.page);
    loadAllCabeleireirosForSelects();
  }
});

async function deleteCabeleireiro(id) {
  if (!await showConfirm('Excluir cabeleireiro #' + id + '?')) return;
  var res = await apiCall('DELETE', '/cabeleireiros/' + id);
  if (res !== null) {
    loadCabeleireiros(cabeleireirosData.page);
    loadAllCabeleireirosForSelects();
  }
}

$('cabeleireiro-filter-form').addEventListener('submit', function(e) {
  e.preventDefault();
  var f = e.target;
  cabeleireirosData.filter = {
    nome: f.nome.value,
    cpf: f.cpf.value || '',
    email: f.email.value || '',
    telefone: f.telefone.value || '',
    especialidade: f.especialidade.value
  };
  loadCabeleireiros(0);
});

function clearCabeleireiroFilter() {
  $('cabeleireiro-filter-form').reset();
  cabeleireirosData.filter = {};
  loadCabeleireiros(0);
}

// ============================================================
// CABELEIREIROS — selects dinâmicos
// ============================================================
var allCabeleireiros = [];

async function loadAllCabeleireirosForSelects() {
  var data = await apiCall('GET', '/cabeleireiros?pagina=0&tamanho-pagina=100');
  if (!data) return;
  allCabeleireiros = data.content || [];
  populateCabeleireiroSelects();
}

function populateCabeleireiroSelects() {
  document.querySelectorAll('select[name="cabeleireiroId"]').forEach(function(sel) {
    var current = sel.value;
    var first = sel.required
      ? '<option value="">Selecione o cabeleireiro...</option>'
      : '<option value="">Todos</option>';
    sel.innerHTML = first + allCabeleireiros.map(function(c) {
      return '<option value="' + c.id + '">' + esc(c.nome) +
        (c.especialidade ? ' — ' + esc(c.especialidade) : '') + '</option>';
    }).join('');
    if (current) sel.value = current;
  });
}

// ============================================================
// CABELEIREIROS — view simplificada para o cliente (na seção de horários)
// ============================================================
var cabListaClienteData = { page: 0 };

async function loadCabeleireirosParaCliente(page) {
  if (page === undefined) page = 0;
  cabListaClienteData.page = page;
  var data = await apiCall('GET', '/cabeleireiros?' + buildQuery({ pagina: page, 'tamanho-pagina': 10 }));
  if (!data) return;
  var tb = $('cab-lista-cliente-tbody');
  var list = data.content || [];
  if (!list.length) { tb.innerHTML = emptyRow(3); }
  else {
    tb.innerHTML = list.map(function(c) {
      return '<tr><td>' + c.id + '</td><td>' + esc(c.nome) + '</td><td>' + esc(c.especialidade || '-') + '</td></tr>';
    }).join('');
  }
  renderPagination('cab-lista-cliente-pagination', data.number, data.totalPages, 'loadCabeleireirosParaCliente');
}

$('horario-cliente-form').addEventListener('submit', async function(e) {
  e.preventDefault();
  var f = e.target;
  var cabId = f.cabeleireiroId.value;
  var dt = f.data.value;
  var data = await apiCall('GET', '/horarios-disponiveis/cabeleireiro/' + cabId + '/data?data=' + htmlDateToApi(dt));
  if (!data) return;
  var list = $('horario-cliente-list');
  list.innerHTML = data.length
    ? data.map(function(h) { return '<li>' + esc(h) + '</li>'; }).join('')
    : '<li>Nenhum hor&aacute;rio dispon&iacute;vel para esta data.</li>';
  $('horario-cliente-results').style.display = 'block';
});

// ============================================================
// AGENDAMENTOS
// ============================================================
var agendamentosData = { list: [], page: 0, filter: {} };
var meusAgendamentosPage = 0;
var meusAgendamentosList = [];
var editingAgendamentoId = null;

async function loadAgendamentos(page) {
  if (page === undefined) page = 0;
  agendamentosData.page = page;
  var data = await apiCall('GET', '/agendamentos?' + buildQuery(
    Object.assign({ pagina: page, 'tamanho-pagina': 10 }, agendamentosData.filter)
  ));
  if (!data) return;
  agendamentosData.list = data.content || [];
  renderAgendamentosTable();
  renderPagination('agendamentos-pagination', data.number, data.totalPages, 'loadAgendamentos');
}

function renderAgendamentosTable() {
  var tb = $('agendamentos-tbody');
  if (!agendamentosData.list.length) { tb.innerHTML = emptyRow(7); return; }
  tb.innerHTML = agendamentosData.list.map(function(a) {
    return '<tr>' +
      '<td>' + a.id + '</td>' +
      '<td>' + esc(a.clienteNome || '#' + a.clienteId) + '</td>' +
      '<td>' + esc(a.cabeleireiroNome || '#' + a.cabeleireiroId) + '</td>' +
      '<td>' + esc(a.dataAgendamento || '-') + '</td>' +
      '<td>' + statusBadge(a.statusAgendamento) + '</td>' +
      '<td>' + (a.sugestao ? '<em class="sugestao">' + esc(a.sugestao) + '</em>' : '-') + '</td>' +
      '<td class="actions">' +
        '<button class="btn-edit" onclick="beginEditAgendamento(' + a.id + ')">Editar</button> ' +
        '<button class="btn-confirm" onclick="confirmarAgendamento(' + a.id + ')">Confirmar</button> ' +
        '<button class="btn-concluir" onclick="concluirAgendamento(' + a.id + ')">Concluir</button> ' +
        '<button class="btn-danger" onclick="deleteAgendamento(' + a.id + ')">Excluir</button>' +
      '</td>' +
    '</tr>';
  }).join('');
}

// Meus agendamentos (cliente)
async function loadMeusAgendamentos(page) {
  if (page === undefined) page = 0;
  meusAgendamentosPage = page;
  if (!myClienteId) {
    $('meus-agendamentos-tbody').innerHTML = emptyRow(5, 'Selecione seu nome no formulário acima para ver seus agendamentos.');
    $('meus-agendamentos-pagination').innerHTML = '';
    return;
  }
  var data = await apiCall('GET', '/agendamentos?' + buildQuery({
    pagina: page,
    'tamanho-pagina': 10,
    'cliente-id': myClienteId
  }));
  if (!data) return;
  var tb = $('meus-agendamentos-tbody');
  meusAgendamentosList = data.content || [];
  if (!meusAgendamentosList.length) {
    tb.innerHTML = emptyRow(6, 'Você não possui agendamentos.');
  } else {
    tb.innerHTML = meusAgendamentosList.map(function(a) {
      return '<tr>' +
        '<td>' + a.id + '</td>' +
        '<td>' + esc(a.cabeleireiroNome || '#' + a.cabeleireiroId) + '</td>' +
        '<td>' + esc(a.dataAgendamento || '-') + '</td>' +
        '<td>' + statusBadge(a.statusAgendamento) + '</td>' +
        '<td>' + esc(a.observacoes || '-') + '</td>' +
        '<td class="actions">' +
          (a.statusAgendamento !== 'CONCLUIDO' && a.statusAgendamento !== 'CANCELADO'
            ? '<button class="btn-edit btn-icon" title="Editar agendamento" onclick="beginEditMeuAgendamento(' + a.id + ')">&#9998;</button> ' +
              '<button class="btn-danger" onclick="cancelarMeuAgendamento(' + a.id + ')">Cancelar</button>'
            : '-') +
        '</td>' +
      '</tr>';
    }).join('');
  }
  renderPagination('meus-agendamentos-pagination', data.number, data.totalPages, 'loadMeusAgendamentos');
}

function beginEditAgendamento(id) {
  var a = agendamentosData.list.find(function(x) { return x.id === id; });
  if (!a) return;
  editingAgendamentoId = id;
  $('agendamento-form-title').textContent = 'Editar Agendamento';
  var f = $('agendamento-form');
  f.clienteId.value = a.clienteId || '';
  f.cabeleireiroId.value = a.cabeleireiroId || '';
  setDateTimeValue(f, 'dataAgendamento', a.dataAgendamento);
  f.observacoes.value = a.observacoes || '';
  $('agendamento-card').scrollIntoView({ behavior: 'smooth', block: 'start' });
}

async function cancelarMeuAgendamento(id) {
  if (!await showConfirm('Cancelar agendamento #' + id + '?')) return;
  var res = await apiCall('PATCH', '/agendamentos/' + id + '/cancelar');
  if (res !== null) {
    showToast('Agendamento #' + id + ' cancelado.', 'info');
    loadMeusAgendamentos(meusAgendamentosPage);
  }
}

function beginEditMeuAgendamento(id) {
  var a = meusAgendamentosList.find(function(x) { return x.id === id; });
  if (!a) return;
  editingAgendamentoId = id;
  $('agendamento-form-title').textContent = 'Editar Agendamento';
  var f = $('agendamento-form');
  f.clienteId.value = myClienteId || '';
  f.cabeleireiroId.value = a.cabeleireiroId || '';
  setDateTimeValue(f, 'dataAgendamento', a.dataAgendamento);
  f.observacoes.value = a.observacoes || '';
  selectedServicos = (a.servicos || []).map(function(s) { return s; });
  renderServicosSelecaoCards();
  updateServicosResumo();
  $('agendamento-card').scrollIntoView({ behavior: 'smooth', block: 'start' });
}

function cancelEditAgendamento() {
  editingAgendamentoId = null;
  $('agendamento-form-title').textContent = 'Novo Agendamento';
  $('agendamento-form').reset();
  selectedServicos = [];
  renderServicosSelecaoCards();
  updateServicosResumo();
}

$('agendamento-form').addEventListener('submit', async function(e) {
  e.preventDefault();
  var f = e.target;
  var body = {
    clienteId: parseInt(f.clienteId.value),
    cabeleireiroId: parseInt(f.cabeleireiroId.value),
    dataAgendamento: getDateTimeValue(f, 'dataAgendamento'),
    observacoes: f.observacoes.value || null,
    servicoIds: selectedServicos.map(function(s) { return s.id; })
  };
  if (!isAdmin) {
    myClienteId = parseInt(f.clienteId.value) || null;
    if (myClienteId) sessionStorage.setItem('_cid', myClienteId);
  }
  var res = editingAgendamentoId
    ? await apiCall('PUT', '/agendamentos/' + editingAgendamentoId, body)
    : await apiCall('POST', '/agendamentos', body);
  if (res !== null) {
    var base = editingAgendamentoId ? 'Agendamento atualizado!' : 'Agendamento cadastrado!';
    showToast(base);
    if (res.sugestao) showToast('Sugestão: ' + res.sugestao, 'info');
    selectedServicos = [];
    cancelEditAgendamento();
    if (isAdmin) loadAgendamentos(agendamentosData.page);
    else loadMeusAgendamentos(meusAgendamentosPage);
  }
});

async function deleteAgendamento(id) {
  if (!await showConfirm('Excluir agendamento #' + id + '?')) return;
  var res = await apiCall('DELETE', '/agendamentos/' + id);
  if (res !== null) loadAgendamentos(agendamentosData.page);
}

async function confirmarAgendamento(id) {
  if (!await showConfirm('Confirmar agendamento #' + id + '?')) return;
  var res = await apiCall('PATCH', '/agendamentos/' + id + '/confirmar');
  if (res !== null) { showToast('Agendamento confirmado!'); loadAgendamentos(agendamentosData.page); }
}

async function concluirAgendamento(id) {
  if (!await showConfirm('Marcar agendamento #' + id + ' como CONCLUÍDO?')) return;
  var res = await apiCall('PATCH', '/agendamentos/' + id + '/concluir');
  if (res !== null) { showToast('Agendamento #' + id + ' concluído!'); loadAgendamentos(agendamentosData.page); }
}

$('agendamento-filter-form').addEventListener('submit', function(e) {
  e.preventDefault();
  var f = e.target;
  agendamentosData.filter = {
    'cliente-id': f.clienteId.value,
    'cabeleireiro-id': f.cabeleireiroId.value,
    'data': getDateTimeValue(f, 'data') || ''
  };
  loadAgendamentos(0);
});

function clearAgendamentoFilter() {
  $('agendamento-filter-form').reset();
  agendamentosData.filter = {};
  loadAgendamentos(0);
}

$('historico-form').addEventListener('submit', async function(e) {
  e.preventDefault();
  var f = e.target;
  var data = await apiCall('GET', '/agendamentos/historico?' + buildQuery({
    'cliente-id': f.clienteId.value,
    'data-inicio': getDateTimeValue(f, 'dataInicio'),
    'data-fim': getDateTimeValue(f, 'dataFim')
  }));
  if (!data) return;
  var tb = $('historico-tbody');
  tb.innerHTML = !data.length ? emptyRow(6, 'Nenhum agendamento no histórico') :
    data.map(function(a) {
      return '<tr><td>' + a.id + '</td><td>' + esc(a.clienteNome || '#' + a.clienteId) + '</td>' +
        '<td>' + esc(a.cabeleireiroNome || '#' + a.cabeleireiroId) + '</td>' +
        '<td>' + esc(a.dataAgendamento || '-') + '</td><td>' + statusBadge(a.statusAgendamento) + '</td>' +
        '<td>' + esc(a.observacoes || '-') + '</td></tr>';
    }).join('');
  $('historico-results').style.display = 'block';
});

$('dia-form').addEventListener('submit', async function(e) {
  e.preventDefault();
  var f = e.target;
  var params = {
    'data-inicio': htmlDateToApi(f.dataInicio.value),
    'data-fim': htmlDateToApi(f.dataFim.value)
  };
  if (f.cabeleireiroId.value) params['cabeleireiro-id'] = f.cabeleireiroId.value;
  var data = await apiCall('GET', '/agendamentos/dia?' + buildQuery(params));
  if (!data) return;
  var tb = $('dia-tbody');
  tb.innerHTML = !data.length ? emptyRow(6, 'Nenhum agendamento para este período') :
    data.map(function(a) {
      return '<tr><td>' + a.id + '</td><td>' + esc(a.clienteNome || '#' + a.clienteId) + '</td>' +
        '<td>' + esc(a.cabeleireiroNome || '#' + a.cabeleireiroId) + '</td>' +
        '<td>' + esc(a.dataAgendamento || '-') + '</td><td>' + statusBadge(a.statusAgendamento) + '</td>' +
        '<td>' + esc(a.observacoes || '-') + '</td></tr>';
    }).join('');
  $('dia-results').style.display = 'block';
});

// ============================================================
// HORÁRIOS DISPONÍVEIS (admin)
// ============================================================
var horariosData = { list: [], page: 0, filter: {} };
var editingHorarioId = null;

async function loadHorarios(page) {
  if (page === undefined) page = 0;
  horariosData.page = page;
  var data = await apiCall('GET', '/horarios-disponiveis?' + buildQuery(
    Object.assign({ pagina: page, tamanhoPagina: 10 }, horariosData.filter)
  ));
  if (!data) return;
  horariosData.list = data.content || [];
  renderHorariosTable();
  renderPagination('horarios-pagination', data.number, data.totalPages, 'loadHorarios');
}

function renderHorariosTable() {
  var tb = $('horarios-tbody');
  if (!horariosData.list.length) { tb.innerHTML = emptyRow(8); return; }
  tb.innerHTML = horariosData.list.map(function(h) {
    return '<tr>' +
      '<td>' + h.id + '</td><td>' + esc(h.cabeleireiroNome || '#' + h.cabeleireiroId) + '</td>' +
      '<td>' + esc(h.diaSemana) + '</td><td>' + apiTimeToHtml(h.horaInicio) + '</td>' +
      '<td>' + apiTimeToHtml(h.horaFim) + '</td><td>' + h.intervaloMinutos + ' min</td>' +
      '<td>' + (h.ativo ? 'Ativo' : 'Inativo') + '</td>' +
      '<td class="actions">' +
        '<button class="btn-edit" onclick="beginEditHorario(' + h.id + ')">Editar</button> ' +
        '<button class="btn-danger" onclick="deleteHorario(' + h.id + ')">Excluir</button>' +
      '</td></tr>';
  }).join('');
}

function beginEditHorario(id) {
  var h = horariosData.list.find(function(x) { return x.id === id; });
  if (!h) return;
  editingHorarioId = id;
  $('horario-form-title').textContent = 'Editar Horário';
  var f = $('horario-form');
  f.cabeleireiroId.value = h.cabeleireiroId || '';
  f.diaSemana.value = h.diaSemana || '';
  f.horaInicio.value = apiTimeToHtml(h.horaInicio);
  f.horaFim.value = apiTimeToHtml(h.horaFim);
  f.intervaloMinutos.value = h.intervaloMinutos || '';
  f.ativo.checked = h.ativo !== false;
  $('horario-card').scrollIntoView({ behavior: 'smooth', block: 'start' });
}

function cancelEditHorario() {
  editingHorarioId = null;
  $('horario-form-title').textContent = 'Novo Horário Disponível';
  $('horario-form').reset();
  $('horario-form').ativo.checked = true;
}

$('horario-form').addEventListener('submit', async function(e) {
  e.preventDefault();
  var f = e.target;
  var body = {
    cabeleireiroId: parseInt(f.cabeleireiroId.value),
    diaSemana: f.diaSemana.value,
    horaInicio: htmlTimeToApi(f.horaInicio.value),
    horaFim: htmlTimeToApi(f.horaFim.value),
    intervaloMinutos: parseInt(f.intervaloMinutos.value),
    ativo: f.ativo.checked
  };
  var res = editingHorarioId
    ? await apiCall('PUT', '/horarios-disponiveis/' + editingHorarioId, body)
    : await apiCall('POST', '/horarios-disponiveis', body);
  if (res !== null) {
    showToast(editingHorarioId ? 'Horário atualizado!' : 'Horário cadastrado!');
    cancelEditHorario();
    loadHorarios(horariosData.page);
  }
});

async function deleteHorario(id) {
  if (!await showConfirm('Excluir horário #' + id + '?')) return;
  var res = await apiCall('DELETE', '/horarios-disponiveis/' + id);
  if (res !== null) loadHorarios(horariosData.page);
}

$('horario-filter-form').addEventListener('submit', function(e) {
  e.preventDefault();
  var f = e.target;
  horariosData.filter = { cabeleireiroId: f.cabeleireiroId.value, diaSemana: f.diaSemana.value, ativo: f.ativo.value };
  loadHorarios(0);
});

function clearHorarioFilter() {
  $('horario-filter-form').reset();
  horariosData.filter = {};
  loadHorarios(0);
}

$('horario-disponivel-form').addEventListener('submit', async function(e) {
  e.preventDefault();
  var f = e.target;
  var data = await apiCall('GET', '/horarios-disponiveis/cabeleireiro/' + f.cabeleireiroId.value + '/data?data=' + htmlDateToApi(f.data.value));
  if (!data) return;
  var list = $('horario-disponivel-list');
  list.innerHTML = data.length
    ? data.map(function(h) { return '<li>' + esc(h) + '</li>'; }).join('')
    : '<li>Nenhum hor&aacute;rio dispon&iacute;vel para esta data.</li>';
  $('horario-disponivel-results').style.display = 'block';
});

// ============================================================
// PAGAMENTOS (admin)
// ============================================================
var pagamentosData = { list: [], page: 0, filter: {} };
var editingPagamentoId = null;

async function loadPagamentos(page) {
  if (page === undefined) page = 0;
  pagamentosData.page = page;
  var data = await apiCall('GET', '/pagamentos?' + buildQuery(
    Object.assign({ pagina: page, 'tamanho-pagina': 10 }, pagamentosData.filter)
  ));
  if (!data) return;
  pagamentosData.list = data.content || [];
  renderPagamentosTable();
  renderPagination('pagamentos-pagination', data.number, data.totalPages, 'loadPagamentos');
}

function renderPagamentosTable() {
  var tb = $('pagamentos-tbody');
  if (!pagamentosData.list.length) { tb.innerHTML = emptyRow(8); return; }
  tb.innerHTML = pagamentosData.list.map(function(p) {
    return '<tr>' +
      '<td>' + p.id + '</td><td>#' + p.agendamentoId + '</td>' +
      '<td>' + esc(p.clienteNome || '-') + '</td>' +
      '<td>R$ ' + Number(p.valor).toFixed(2) + '</td>' +
      '<td>' + esc(p.formaPagamento) + '</td>' +
      '<td>' + statusBadge(p.statusPagamento) + '</td>' +
      '<td>' + esc(p.dataPagamento || '-') + '</td>' +
      '<td class="actions">' +
        '<button class="btn-edit" onclick="beginEditPagamento(' + p.id + ')">Editar</button> ' +
        '<button class="btn-confirm" onclick="marcarComoPago(' + p.id + ')">Marcar Pago</button> ' +
        '<button class="btn-danger" onclick="deletePagamento(' + p.id + ')">Excluir</button>' +
      '</td></tr>';
  }).join('');
}

function beginEditPagamento(id) {
  var p = pagamentosData.list.find(function(x) { return x.id === id; });
  if (!p) return;
  editingPagamentoId = id;
  $('pagamento-form-title').textContent = 'Editar Pagamento';
  var f = $('pagamento-form');
  f.agendamentoId.value = p.agendamentoId || '';
  f.valor.value = p.valor || '';
  f.formaPagamento.value = p.formaPagamento || '';
  f.statusPagamento.value = p.statusPagamento || '';
  setDateTimeValue(f, 'dataPagamento', p.dataPagamento);
  f.observacoes.value = p.observacoes || '';
  $('pagamento-card').scrollIntoView({ behavior: 'smooth', block: 'start' });
}

function cancelEditPagamento() {
  editingPagamentoId = null;
  $('pagamento-form-title').textContent = 'Novo Pagamento';
  $('pagamento-form').reset();
}

$('pagamento-form').addEventListener('submit', async function(e) {
  e.preventDefault();
  var f = e.target;
  var body = {
    agendamentoId: parseInt(f.agendamentoId.value),
    valor: parseFloat(f.valor.value),
    formaPagamento: f.formaPagamento.value,
    statusPagamento: f.statusPagamento.value,
    dataPagamento: getDateTimeValue(f, 'dataPagamento'),
    observacoes: f.observacoes.value || null
  };
  var res = editingPagamentoId
    ? await apiCall('PUT', '/pagamentos/' + editingPagamentoId, body)
    : await apiCall('POST', '/pagamentos', body);
  if (res !== null) {
    showToast(editingPagamentoId ? 'Pagamento atualizado!' : 'Pagamento cadastrado!');
    cancelEditPagamento();
    loadPagamentos(pagamentosData.page);
  }
});

async function deletePagamento(id) {
  if (!await showConfirm('Excluir pagamento #' + id + '?')) return;
  var res = await apiCall('DELETE', '/pagamentos/' + id);
  if (res !== null) loadPagamentos(pagamentosData.page);
}

async function marcarComoPago(id) {
  if (!await showConfirm('Marcar pagamento #' + id + ' como PAGO?')) return;
  var res = await apiCall('PUT', '/pagamentos/' + id, { statusPagamento: 'PAGO' });
  if (res !== null) {
    showToast('Pagamento #' + id + ' marcado como PAGO!');
    loadPagamentos(pagamentosData.page);
  }
}

$('pagamento-filter-form').addEventListener('submit', function(e) {
  e.preventDefault();
  var f = e.target;
  pagamentosData.filter = {
    'agendamento-id': f.agendamentoId.value,
    'status-pagamento': f.statusPagamento.value,
    'forma-pagamento': f.formaPagamento.value
  };
  loadPagamentos(0);
});

function clearPagamentoFilter() {
  $('pagamento-filter-form').reset();
  pagamentosData.filter = {};
  loadPagamentos(0);
}

$('relatorio-pagamento-form').addEventListener('submit', async function(e) {
  e.preventDefault();
  var f = e.target;
  var data = await apiCall('GET', '/pagamentos/relatorio?' + buildQuery({
    cabeleireiroId: f.cabeleireiroId.value,
    dataInicio: getDateTimeValue(f, 'dataInicio'),
    dataFim: getDateTimeValue(f, 'dataFim')
  }));
  if (!data) return;
  var rows = (data.pagamentos || []).map(function(p) {
    return '<tr><td>' + p.id + '</td><td>' + esc(p.clienteNome || '-') + '</td>' +
      '<td>R$ ' + Number(p.valor).toFixed(2) + '</td><td>' + esc(p.formaPagamento) + '</td>' +
      '<td>' + statusBadge(p.statusPagamento) + '</td><td>' + esc(p.dataPagamento || '-') + '</td></tr>';
  }).join('');
  var result = $('relatorio-pagamento-result');
  result.innerHTML =
    '<div class="relatorio-grid">' +
      '<div class="relatorio-item"><span class="relatorio-label">Cabeleireiro</span><span class="relatorio-value">' + esc(data.cabeleireiroNome || '-') + '</span></div>' +
      '<div class="relatorio-item"><span class="relatorio-label">Total Faturado</span><span class="relatorio-value highlight">R$ ' + Number(data.totalFaturado || 0).toFixed(2) + '</span></div>' +
      '<div class="relatorio-item"><span class="relatorio-label">Total Atendimentos</span><span class="relatorio-value">' + (data.totalAtendimentos || 0) + '</span></div>' +
    '</div>' +
    (rows ? '<div class="table-wrapper"><table><thead><tr><th>ID</th><th>Cliente</th><th>Valor</th><th>Forma</th><th>Status</th><th>Data</th></tr></thead><tbody>' + rows + '</tbody></table></div>' : '');
  result.style.display = 'block';
});

// ============================================================
// RELATÓRIO SEMANAL (admin)
// ============================================================
$('relatorio-semanal-form').addEventListener('submit', async function(e) {
  e.preventDefault();
  var f = e.target;
  var data = await apiCall('GET', '/relatorios/semanal?' + buildQuery({
    'data-inicio': getDateTimeValue(f, 'dataInicio'),
    'data-fim': getDateTimeValue(f, 'dataFim')
  }));
  if (!data) return;
  var formaRows = '';
  if (data.faturamentoPorFormaPagamento) {
    for (var forma in data.faturamentoPorFormaPagamento) {
      formaRows += '<tr><td>' + esc(forma) + '</td><td>R$ ' + Number(data.faturamentoPorFormaPagamento[forma]).toFixed(2) + '</td></tr>';
    }
  }
  var result = $('relatorio-semanal-result');
  result.innerHTML =
    '<h3>Resultado do Relat&oacute;rio Semanal</h3>' +
    '<div class="relatorio-grid" style="margin-top:16px">' +
      '<div class="relatorio-item"><span class="relatorio-label">Total Agendamentos</span><span class="relatorio-value highlight">' + (data.totalAgendamentos || 0) + '</span></div>' +
      '<div class="relatorio-item"><span class="relatorio-label">Total Faturado</span><span class="relatorio-value highlight">R$ ' + Number(data.totalFaturado || 0).toFixed(2) + '</span></div>' +
      '<div class="relatorio-item"><span class="relatorio-label">Confirmados</span><span class="relatorio-value">' + (data.totalConfirmados || 0) + '</span></div>' +
      '<div class="relatorio-item"><span class="relatorio-label">Cancelados</span><span class="relatorio-value">' + (data.totalCancelados || 0) + '</span></div>' +
      '<div class="relatorio-item"><span class="relatorio-label">Conclu&iacute;dos</span><span class="relatorio-value">' + (data.totalConcluidos || 0) + '</span></div>' +
      '<div class="relatorio-item"><span class="relatorio-label">Cabeleireiro Destaque</span><span class="relatorio-value">' + esc(data.cabeleireiroMaisAtendimentos || '-') + '</span></div>' +
      '<div class="relatorio-item"><span class="relatorio-label">Dia Mais Movimentado</span><span class="relatorio-value">' + esc(data.diaMaisAtendimentos || '-') + '</span></div>' +
    '</div>' +
    (formaRows ? '<h4 style="margin-bottom:10px">Faturamento por Forma de Pagamento</h4><div class="table-wrapper"><table><thead><tr><th>Forma</th><th>Total</th></tr></thead><tbody>' + formaRows + '</tbody></table></div>' : '');
  result.style.display = 'block';
});

// ============================================================
// SERVIÇOS
// ============================================================
var servicosData = { list: [], page: 0 };
var editingServicoId = null;
var allServicos = [];
var selectedServicos = [];

function formatDuracao(min) {
  if (!min) return '-';
  var h = Math.floor(min / 60);
  var m = min % 60;
  if (h === 0) return min + ' min';
  return m > 0 ? h + 'h' + String(m).padStart(2, '0') : h + 'h';
}

function formatValor(v) {
  return 'R$ ' + Number(v || 0).toFixed(2).replace('.', ',');
}

// --- Consulta (leitura, admin + cliente) ---
async function loadServicos(page) {
  if (page === undefined) page = 0;
  servicosData.page = page;
  var query = 'pagina=' + page + '&tamanho-pagina=12' + (!isAdmin ? '&ativo=true' : '');
  var data = await apiCall('GET', '/servicos?' + query);
  if (!data) return;
  servicosData.list = data.content || [];
  renderServicosCards();
  renderPagination('servicos-pagination', data.number, data.totalPages, 'loadServicos');
}

function renderServicosCards() {
  var grid = $('servicos-cards-grid');
  if (!servicosData.list.length) {
    grid.innerHTML = '<p class="empty-msg" style="padding:32px 0">Nenhum servi&ccedil;o cadastrado.</p>';
    return;
  }
  grid.innerHTML = servicosData.list.map(function(s) {
    var adminBtns = isAdmin
      ? '<div class="sc-admin-btns">' +
          '<button class="btn-edit" onclick="beginEditServico(' + s.id + ')">Editar</button> ' +
          '<button class="btn-danger" onclick="deleteServico(' + s.id + ')">Excluir</button>' +
        '</div>'
      : '';
    return '<div class="servico-consulta-card">' +
      '<div class="servico-consulta-top">' +
        '<span class="servico-consulta-nome">' + esc(s.nome) + '</span>' +
        (isAdmin ? '<span class="badge ' + (s.ativo ? 'badge-pago' : 'badge-cancelado') + '">' + (s.ativo ? 'Ativo' : 'Inativo') + '</span>' : '') +
      '</div>' +
      '<div class="servico-consulta-body">' +
        '<p class="servico-consulta-desc">' + esc(s.descricao || '') + '</p>' +
        '<div class="servico-consulta-footer">' +
          '<span class="servico-consulta-duracao">' + formatDuracao(s.duracaoMinutos) + '</span>' +
          '<span class="servico-consulta-valor">' + formatValor(s.preco) + '</span>' +
        '</div>' +
        adminBtns +
      '</div>' +
    '</div>';
  }).join('');
}

// --- Admin CRUD ---
function beginEditServico(id) {
  var s = servicosData.list.find(function(x) { return x.id === id; });
  if (!s) return;
  editingServicoId = id;
  $('servico-form-title').textContent = 'Editar Serviço';
  var f = $('servico-form');
  f.nome.value = s.nome || '';
  f.descricao.value = s.descricao || '';
  f.duracaoMinutos.value = s.duracaoMinutos || '';
  f.valor.value = s.preco || '';
  f.ativo.checked = s.ativo !== false;
  $('servico-card').scrollIntoView({ behavior: 'smooth', block: 'start' });
}

function cancelEditServico() {
  editingServicoId = null;
  $('servico-form-title').textContent = 'Novo Serviço';
  $('servico-form').reset();
  $('servico-form').ativo.checked = true;
}

$('servico-form').addEventListener('submit', async function(e) {
  e.preventDefault();
  var f = e.target;
  var body = {
    nome: f.nome.value,
    descricao: f.descricao.value || null,
    duracaoMinutos: parseInt(f.duracaoMinutos.value),
    preco: parseFloat(f.valor.value),
    ativo: f.ativo.checked
  };
  var res = editingServicoId
    ? await apiCall('PUT', '/servicos/' + editingServicoId, body)
    : await apiCall('POST', '/servicos', body);
  if (res !== null) {
    showToast(editingServicoId ? 'Serviço atualizado!' : 'Serviço cadastrado!');
    cancelEditServico();
    loadServicos(servicosData.page);
    loadServicosParaSelecao();
  }
});

async function deleteServico(id) {
  if (!await showConfirm('Excluir serviço #' + id + '?')) return;
  var res = await apiCall('DELETE', '/servicos/' + id);
  if (res !== null) {
    loadServicos(servicosData.page);
    loadServicosParaSelecao();
  }
}

// --- Seleção no formulário de agendamento ---
async function loadServicosParaSelecao() {
  var data = await apiCall('GET', '/servicos?pagina=0&tamanho-pagina=100');
  if (!data) return;
  allServicos = (data.content || []).filter(function(s) { return s.ativo; });
  renderServicosSelecaoCards();
}

function renderServicosSelecaoCards() {
  var grid = $('servicos-selecao-grid');
  if (!grid) return;
  if (!allServicos.length) {
    grid.innerHTML = '<p style="color:#bbb;font-size:0.85rem;padding:6px 0">Nenhum servi&ccedil;o dispon&iacute;vel.</p>';
    $('servicos-resumo').style.display = 'none';
    return;
  }
  var selectedIds = selectedServicos.map(function(s) { return s.id; });
  grid.innerHTML = allServicos.map(function(s) {
    var isSel = selectedIds.indexOf(s.id) >= 0;
    return '<div class="servico-selecao-card' + (isSel ? ' selected' : '') + '" onclick="toggleServico(' + s.id + ')">' +
      '<div class="sc-nome">' + esc(s.nome) + '</div>' +
      '<div class="sc-info">' + formatDuracao(s.duracaoMinutos) + ' &middot; ' + formatValor(s.preco) + '</div>' +
    '</div>';
  }).join('');
}

function toggleServico(id) {
  var servico = allServicos.find(function(s) { return s.id === id; });
  if (!servico) return;
  var idx = -1;
  for (var i = 0; i < selectedServicos.length; i++) {
    if (selectedServicos[i].id === id) { idx = i; break; }
  }
  if (idx >= 0) selectedServicos.splice(idx, 1);
  else selectedServicos.push(servico);
  renderServicosSelecaoCards();
  updateServicosResumo();
}

function updateServicosResumo() {
  var resumo = $('servicos-resumo');
  if (!resumo) return;
  if (!selectedServicos.length) { resumo.style.display = 'none'; return; }
  resumo.style.display = 'flex';
  $('resumo-nomes').textContent = selectedServicos.map(function(s) { return s.nome; }).join(', ');
  var totalMin = selectedServicos.reduce(function(acc, s) { return acc + (s.duracaoMinutos || 0); }, 0);
  $('resumo-duracao').textContent = formatDuracao(totalMin);
  var totalValor = selectedServicos.reduce(function(acc, s) { return acc + Number(s.preco || 0); }, 0);
  $('resumo-valor').textContent = formatValor(totalValor);
}

// ============================================================
// INIT
// ============================================================
document.addEventListener('DOMContentLoaded', function() {
  initDateMasks();
  initTimeSelects();
  if (auth.isLoggedIn()) {
    applyRole();
    myClienteId = parseInt(sessionStorage.getItem('_cid')) || null;
    renderApp();
    showSection(isAdmin ? 'clientes' : 'cabeleireiros');
  } else {
    renderLogin();
  }
});
