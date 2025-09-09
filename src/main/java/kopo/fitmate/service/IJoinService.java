package kopo.fitmate.service;

import kopo.fitmate.dto.JoinDTO;

public interface IJoinService {

    /**
     * 회원 가입 (사용자 정보 등록)
     */
    void insertUserInfo(JoinDTO pDTO) throws Exception;


}
