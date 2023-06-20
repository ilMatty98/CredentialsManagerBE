package com.credentialsmanager.controller;

import com.credentialsmanager.dto.request.ChangePasswordDto;
import com.credentialsmanager.dto.request.LogInDto;
import com.credentialsmanager.dto.request.SignUpDto;
import com.credentialsmanager.dto.response.AccessDto;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.credentialsmanager.constants.UrlConstants.*;

@RestController
@Tag(name = "Authentication")
@RequestMapping(BASE_PATH_AUTHENTICATION)
public interface AuthenticationController {

    @PostMapping(SIGN_UP)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "SignUp successfully"),
            @ApiResponse(responseCode = "400", description = "Bad Request")})
    ResponseEntity<Object> signUp(@Parameter(description = "Dto to create a new user")
                                  @Valid @RequestBody SignUpDto signUpDto);

    @PostMapping(LOG_IN)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "LogIn successfully"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")})
    AccessDto logIn(@Parameter(description = "Dto to log in")
                    @Valid @RequestBody LogInDto logInDto);

    @GetMapping(CHECK_EMAIL)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Checking on email done"),
            @ApiResponse(responseCode = "400", description = "Bad Request")})
    boolean checkEmail(@RequestHeader(HEADER_EMAIL) String email);

    @PatchMapping(CONFIRM_EMAIL)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email successfully confirmed"),
            @ApiResponse(responseCode = "404", description = "Not found")})
    ResponseEntity<Object> confirmEmail(@PathVariable String email, @PathVariable String code);

    @PutMapping(CHANGE_PASSWORD)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password successfully changed"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Not found")})
    ResponseEntity<Object> changePassword(@Parameter(description = "Dto to change password")
                                          @Valid @RequestBody ChangePasswordDto changePasswordDto,
                                          HttpServletRequest request);

    @PostMapping(SEND_HINT)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Checking on email done"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "404", description = "Not found")})
    ResponseEntity<Object> sendHint(@RequestHeader(HEADER_EMAIL) String email);

    @DeleteMapping(DELETE_ACCOUNT)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Not found")})
    ResponseEntity<Object> deleteAccount(HttpServletRequest request);
}
