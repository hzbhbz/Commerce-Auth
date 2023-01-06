package com.digital.service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

/* 
 * Token의 유효제한지간 확인, 30분간 아무런 액션이 없으면, Token은 삭제되어아함.
 *      Token정보로 PersonID가 메모리에 존재하는지 확인 후
        유효한 Token인지를 리턴한다.
        리턴 전 Token의 생성 시간도 갱신해야함
        유효하지 않으면, 로그인 페이지로 redirec하거나, 에러처리
        유효하면 요청한 서비스를 실행함.
*/

@Component
public class AuthService {

	// Static Map <Token, Map<PersonID, 생성시간>>
	public static Map<String, Map<Long, Long>> tokenMap;

	// Token 생성
	public String setToken(long personId) throws Exception {

		if (tokenMap == null) {
			tokenMap = new HashMap<String, Map<Long, Long>>();
		}
		Map<Long, Long> authInfoMap = new HashMap<Long, Long>();
		authInfoMap.put(personId, System.currentTimeMillis());

		String token = String.valueOf(System.currentTimeMillis());

		tokenMap.put(token, authInfoMap);
		
		System.out.println(tokenMap);

		return token;
	}

	// Token 유효성 검사
	public boolean isValidToken(String token) throws Exception {
		
		if (tokenMap.get(token) != null && tokenMap != null) {
			return true;
		}
		return false;
	}

	// Token 유효 시간 검사
	public boolean isExpiredToken(String token) throws Exception {

		Map<Long, Long> authInfoMap = tokenMap.get(token);
		System.out.println(authInfoMap);
		
		Iterator<Long> keys = authInfoMap.keySet().iterator();

		while (keys.hasNext()) {
			long personId = keys.next();

			long start = authInfoMap.get(personId);
			long currentTime = System.currentTimeMillis();

			long elapse = currentTime - start;

			if (elapse > 30 * 60 * 1000) {
				// 만료 시간, Token 삭제
				tokenMap.remove(token);
				return false;
			}
			// 만료 시간이 지나지 않았으면 유효 시간 갱신
			updateValidTime(token);
		}
		return true;
	}

	// Token 유효 시간 갱신
	public void updateValidTime(String token) throws Exception {

		Map<Long, Long> authInfoMap = tokenMap.get(token);
		Iterator<Long> keys = authInfoMap.keySet().iterator();

		while (keys.hasNext()) {
			long personId = keys.next();

			authInfoMap.put(personId, System.currentTimeMillis());
			tokenMap.put(token, authInfoMap);

		}
	}

	// personId 찾기
	public long getPersonId(String token) throws Exception {
		System.out.println(tokenMap);
		Map<Long, Long> authMap = tokenMap.get(token);
		Set<Long> set = authMap.keySet();
		Iterator<Long> iterator = set.iterator();

		if (iterator.hasNext()) {
			return iterator.next();
		}
		return 0;
	}
}