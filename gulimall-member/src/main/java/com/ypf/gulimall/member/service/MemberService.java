package com.ypf.gulimall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ypf.common.utils.PageUtils;
import com.ypf.gulimall.member.entity.MemberEntity;
import com.ypf.gulimall.member.entity.MemberEntity;
import com.ypf.gulimall.member.exception.EmailExistException;
import com.ypf.gulimall.member.exception.UsernameExistException;
import com.ypf.gulimall.member.vo.MemberLoginVo;
import com.ypf.gulimall.member.vo.MemberRegistVo;

import java.util.Map;

/**
 * 会员
 *
 * @author leifengyang
 * @email leifengyang@gmail.com
 * @date 2019-10-08 09:47:05
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void regist(MemberRegistVo vo);
    void checkEmailUnique(String email) throws EmailExistException;
    void checkUsernameUnique(String username) throws UsernameExistException;

    MemberEntity login(MemberLoginVo memberLoginVo);
}

