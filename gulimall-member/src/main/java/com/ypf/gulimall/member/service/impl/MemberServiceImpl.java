package com.ypf.gulimall.member.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.segments.MergeSegments;
import com.ypf.gulimall.member.dao.MemberLevelDao;
import com.ypf.gulimall.member.entity.MemberEntity;
import com.ypf.gulimall.member.entity.MemberLevelEntity;
import com.ypf.gulimall.member.exception.EmailExistException;
import com.ypf.gulimall.member.exception.UsernameExistException;
import com.ypf.gulimall.member.service.MemberService;
import com.ypf.gulimall.member.vo.MemberLoginVo;
import com.ypf.gulimall.member.vo.MemberRegistVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ypf.common.utils.PageUtils;
import com.ypf.common.utils.Query;

import com.ypf.gulimall.member.dao.MemberDao;
import com.ypf.gulimall.member.entity.MemberEntity;
import com.ypf.gulimall.member.service.MemberService;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {
    @Autowired
    MemberLevelDao memberLevelDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void regist(MemberRegistVo vo) {
        MemberEntity memberEntity = new MemberEntity();
        MemberLevelEntity memberLevelEntity = memberLevelDao.getDefaultLevel();

        memberEntity.setLevelId(memberLevelEntity.getId());
        memberEntity.setUsername(vo.getUsername());
        memberEntity.setEmail(vo.getEmail());

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();       String encode = encoder.encode(vo.getPassword());
        memberEntity.setPassword(encode);
        memberEntity.setNickname(vo.getUsername());
        this.baseMapper.insert(memberEntity);
        //异常机制 感知异常
    }

    @Override
    public void checkEmailUnique(String email) throws EmailExistException {
        Integer count = this.baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("mobile", email));
        if (count > 0) {

        } else {
            throw new EmailExistException();
        }

    }

    @Override
    public void checkUsernameUnique(String username) throws UsernameExistException {
        Integer count = this.baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("username", username));
        if (count > 0) {

        } else {
            throw new UsernameExistException();
        }
    }

    @Override
    public MemberEntity login(MemberLoginVo memberLoginVo) {
        String loginacct = memberLoginVo.getLoginacct();
        String password = memberLoginVo.getPassword();
        MemberEntity memberEntity = this.baseMapper.selectOne(new QueryWrapper<MemberEntity>().eq("username", loginacct)
                .or().eq("email", loginacct).or().eq("mobile", loginacct)
        );
        if (memberEntity == null) {
            return null;
        } else {
            String passwordDb = memberEntity.getPassword();
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            if (encoder.matches(password, passwordDb)) {
                return memberEntity;
            } else {
                return null;
            }
        }
    }

}