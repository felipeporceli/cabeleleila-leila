package com.cabeleleilaleila.demo.dto;

import lombok.Builder;

@Builder
public record ErroCampo(String campo, String erro) {
}
