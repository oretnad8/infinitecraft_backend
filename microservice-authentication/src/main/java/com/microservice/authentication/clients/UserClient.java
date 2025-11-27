package com.microservice.authentication.clients;

import com.microservice.authentication.dto.RegisterRequest;
import com.microservice.authentication.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;

@FeignClient(name = "microservice-user", url = "${user.service.url:http://fotomarwmsdb.ddns.net:8082}", path = "/users")
public interface UserClient {

    @GetMapping("/search/email/{email}")
    Optional<UserDto> findByEmail(@PathVariable String email);

    @PostMapping
    UserDto save(@RequestBody UserDto user);
}
