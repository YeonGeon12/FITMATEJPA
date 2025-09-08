package kopo.fitmate.service;

import kopo.fitmate.dto.UserDTO;

public interface IUserService {

    /**
     * 회원 가입 (사용자 정보 등록)
     */
    void insertUserInfo(UserDTO pDTO) throws Exception;


}
