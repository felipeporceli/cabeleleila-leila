-- Criação das sequences
CREATE SEQUENCE IF NOT EXISTS public.cabeleleiro_id_seq;

-- Criação das tabelas
CREATE TABLE IF NOT EXISTS public.cliente (
    id serial NOT NULL,
    nome character varying(200) NOT NULL,
    cpf character varying(15) NOT NULL,
    senha character varying(250) NOT NULL,
    email character varying(200) NOT NULL,
    telefone character varying(25),
    logradouro character varying(200),
    bairro character varying(200),
    numero character varying(5),
    cidade character varying(200),
    complemento character varying(200),
    cep character varying(10),
    CONSTRAINT cliente_pkey PRIMARY KEY (id),
    CONSTRAINT cliente_cpf_key UNIQUE (cpf),
    CONSTRAINT cliente_email_key UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS public.cabeleireiro (
    id integer NOT NULL DEFAULT nextval('cabeleleiro_id_seq'::regclass),
    nome character varying(200) NOT NULL,
    cpf character varying(15) NOT NULL,
    senha character varying(250) NOT NULL,
    email character varying(200) NOT NULL,
    telefone character varying(25),
    especialidade character varying(50) NOT NULL,
    CONSTRAINT cabeleleiro_pkey PRIMARY KEY (id),
    CONSTRAINT cabeleleiro_cpf_key UNIQUE (cpf),
    CONSTRAINT cabeleleiro_email_key UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS public.servico (
    id serial NOT NULL,
    nome character varying(200) NOT NULL,
    descricao character varying(200),
    preco numeric(10, 2) NOT NULL,
    duracao_minutos integer NOT NULL,
    ativo boolean NOT NULL DEFAULT true,
    CONSTRAINT servico_pkey PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS public.horario_disponivel (
    id serial NOT NULL,
    cabeleireiro_id integer NOT NULL,
    dia_semana character varying(10) NOT NULL,
    hora_inicio time without time zone NOT NULL,
    hora_fim time without time zone NOT NULL,
    intervalo_minutos integer NOT NULL,
    ativo boolean NOT NULL DEFAULT true,
    CONSTRAINT horario_disponivel_pkey PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS public.agendamento (
    id serial NOT NULL,
    cliente_id integer NOT NULL,
    cabeleireiro_id integer NOT NULL,
    data_agendamento timestamp without time zone NOT NULL,
    observacoes character varying(200),
    status_agendamento character varying(20) NOT NULL DEFAULT 'AGENDADO',
    CONSTRAINT agendamento_pkey PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS public.agendamento_servico (
    id serial NOT NULL,
    agendamento_id integer NOT NULL,
    servico_id integer NOT NULL,
    CONSTRAINT agendamento_servico_pkey PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS public.pagamento (
    id serial NOT NULL,
    agendamento_id integer NOT NULL,
    valor numeric(10, 2) NOT NULL,
    forma_pagamento character varying(20) NOT NULL,
    status_pagamento character varying(20) NOT NULL DEFAULT 'PENDENTE',
    data_pagamento timestamp without time zone,
    observacoes character varying(200),
    CONSTRAINT pagamento_pkey PRIMARY KEY (id),
    CONSTRAINT pagamento_agendamento_id_key UNIQUE (agendamento_id)
);

-- Foreign Keys
ALTER TABLE IF EXISTS public.agendamento
    ADD CONSTRAINT fk_cabeleleiro FOREIGN KEY (cabeleireiro_id) REFERENCES public.cabeleireiro (id) ON DELETE CASCADE;

ALTER TABLE IF EXISTS public.agendamento
    ADD CONSTRAINT fk_cliente FOREIGN KEY (cliente_id) REFERENCES public.cliente (id) ON DELETE CASCADE;

ALTER TABLE IF EXISTS public.agendamento_servico
    ADD CONSTRAINT fk_agendamento FOREIGN KEY (agendamento_id) REFERENCES public.agendamento (id) ON DELETE CASCADE;

ALTER TABLE IF EXISTS public.agendamento_servico
    ADD CONSTRAINT fk_servico FOREIGN KEY (servico_id) REFERENCES public.servico (id) ON DELETE CASCADE;

ALTER TABLE IF EXISTS public.horario_disponivel
    ADD CONSTRAINT fk_cabeleireiro_horario FOREIGN KEY (cabeleireiro_id) REFERENCES public.cabeleireiro (id) ON DELETE CASCADE;

ALTER TABLE IF EXISTS public.pagamento
    ADD CONSTRAINT fk_agendamento_pagamento FOREIGN KEY (agendamento_id) REFERENCES public.agendamento (id) ON DELETE CASCADE;

CREATE INDEX IF NOT EXISTS pagamento_agendamento_id_key ON public.pagamento(agendamento_id);

-- Insert
INSERT INTO public.cliente (id, nome, cpf, senha, email, telefone, logradouro, bairro, numero, cidade, complemento, cep)
VALUES (
    5,
    'Felipe Porceli Volpe',
    '622.522.170-06',
    '213',
    'felipe.porceliv@gmail.com',
    '14997778899',
    'Rua da Cabiuna',
    'Vila Independencia',
    '5',
    'Assis',
    'Casa 37',
    '19914130'
);