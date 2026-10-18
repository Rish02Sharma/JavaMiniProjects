package org.example.SpringBoot.AuthREST;

import java.math.BigDecimal;

public record OrderResponse(Long id, String status, BigDecimal total) {}

