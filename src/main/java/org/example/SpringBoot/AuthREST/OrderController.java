package org.example.SpringBoot.AuthREST;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.designPatterns.solidPrinciples.DependencyInversionPrincipleNotes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
class OrderController {

    private final OrderService service;

    @PostMapping
    ResponseEntity<OrderResponse> create(@Valid @RequestBody CreateOrderRequest req) {
        OrderResponse created = service.create(req);
        URI location = URI.create("/api/v1/orders/" + created.id());
        return ResponseEntity.created(location).body(created);      // 201 + Location
    }

    @GetMapping("/{id}")
    OrderResponse get(@PathVariable Long id) {
        return service.get(id);                                     // 200
    }

    @GetMapping
    ResponseEntity<OrderResponse> list(@RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "20") int size) {
//        return service.get(PageRequest.of(page, size, Sort.by("id").descending()));

        return null;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)                          // 204
    void delete(@PathVariable Long id) { service.delete(id); }
}
