package com.digital.controller;

import javax.annotation.Resource;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.digital.schema.Auth;
import com.digital.schema.ErrorMsg;
import com.digital.service.AuthService;
import com.digital.utils.ExceptionUtils;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "인증", description = "Auth Related API")
@RequestMapping(value = "/rest/auth")

public class AuthController {
	
	@Resource
	AuthService authSvc;
	
	@RequestMapping(value = "/validtoken", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "토큰 검증", notes = "토큰의 유효성을 검증")
	@ApiResponses({
		@ApiResponse(code = 200, message = "성공", response = Auth.class),
		@ApiResponse(code = 500, message = "실패", response = ErrorMsg.class)
	})
	public ResponseEntity<?> validToken (@Parameter(name = "토큰", required = false) @RequestBody Auth auth) {
		MultiValueMap<String, String> header = new LinkedMultiValueMap<String, String>();
		ErrorMsg errors = new ErrorMsg();
		
		Auth auth_res = new Auth();
		try {
			if (authSvc.isValidToken(auth.getToken())) {
				auth_res.setValidToken(true);
			} else {
				auth_res.setValidToken(false);
			}
		} catch (Exception e) {
			return ExceptionUtils.setException(errors, 500, e.getMessage(), header);
		}
		
		return new ResponseEntity<Auth>(auth_res, header, HttpStatus.valueOf(200));
	}
	
	@RequestMapping(value = "/checkexpire", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "토큰 유효시간 검증", notes = "토큰의 유효시간을 검증")
	@ApiResponses({
		@ApiResponse(code = 200, message = "성공", response = Auth.class),
		@ApiResponse(code = 500, message = "실패", response = ErrorMsg.class)
	})
	public ResponseEntity<?> checkExpire (@Parameter(name = "토큰", required = false) @RequestBody Auth auth) {
		MultiValueMap<String, String> header = new LinkedMultiValueMap<String, String>();
		ErrorMsg errors = new ErrorMsg();

		Auth auth_res = new Auth();
		try {
			if (authSvc.isExpiredToken(auth.getToken())) {
				auth_res.setExpiredToken(true);
			} else {
				auth_res.setExpiredToken(false);
			}
		} catch (Exception e) {
			return ExceptionUtils.setException(errors, 500, e.getMessage(), header);
		}
		
		return new ResponseEntity<Auth>(auth_res, header, HttpStatus.valueOf(200));
	}

	@RequestMapping(value = "/generatetoken", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "토큰 생성", notes = "로그인 정보를 사용하여 토큰을 생성")
	@ApiResponses({
		@ApiResponse(code = 200, message = "성공", response = Auth.class),
		@ApiResponse(code = 500, message = "실패", response = ErrorMsg.class)
	})
	public ResponseEntity<?> generateToken (@Parameter(name = "토큰", required = false) @RequestBody Auth auth) {
		MultiValueMap<String, String> header = new LinkedMultiValueMap<String, String>();
		ErrorMsg errors = new ErrorMsg();
		
		Auth auth_res = new Auth();
		try {
			String token = authSvc.setToken(auth.getPersonId());
			auth_res.setToken(token);
			
		} catch (Exception e) {
			return ExceptionUtils.setException(errors, 500, e.getMessage(), header);
		}
		
		return new ResponseEntity<Auth>(auth_res, header, HttpStatus.valueOf(200));
	}
	
	@RequestMapping(value = "/personinfo", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "사용자 ID 확인", notes = "토큰 정보를 이용하여 사용자 ID를 확인한다.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "성공", response = Auth.class),
		@ApiResponse(code = 500, message = "실패", response = ErrorMsg.class)
	})
	public ResponseEntity<?> getPersonId (@Parameter(name = "사용자 ID", required = false)@RequestBody Auth auth) {
		MultiValueMap<String, String> header = new LinkedMultiValueMap<String, String>();
		ErrorMsg errors = new ErrorMsg();

		Auth authRes = new Auth();
		try {
			long personId = authSvc.getPersonId(auth.getToken());
			authRes.setPersonId(personId);
			
		} catch (Exception e) {
			return ExceptionUtils.setException(errors, 500, e.getMessage(), header);
		}
		
		return new ResponseEntity<Auth>(authRes, header, HttpStatus.valueOf(200));
	}
}
