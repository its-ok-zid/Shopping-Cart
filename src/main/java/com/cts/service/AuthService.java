package com.cts.service;

import com.cts.dto.LoginRequestDTO;
import com.cts.dto.LoginResponseDTO;
import com.cts.dto.SignUpRequestDTO;
import com.cts.dto.SignUpResponseDTO;

public interface AuthService {

    LoginResponseDTO login(LoginRequestDTO request);

    SignUpResponseDTO signUp(SignUpRequestDTO request);
}
