package com.hotrodoan.controller;

import com.hotrodoan.dto.request.ChangePasswordForm;
import com.hotrodoan.dto.response.ResponseMessage;
import com.hotrodoan.model.Member;
import com.hotrodoan.model.User;
import com.hotrodoan.security.jwt.JwtProvider;
import com.hotrodoan.security.jwt.JwtTokenFilter;
import com.hotrodoan.service.ProfileService;
import com.hotrodoan.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/profile")
@CrossOrigin(origins = "*")
public class ProfileController {
    @Autowired
    private ProfileService profileService;

    @Autowired
    private JwtTokenFilter jwtTokenFilter;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("")
//    public ResponseEntity<?> getProfile(HttpServletRequest request) {
//        String jwt = jwtTokenFilter.getJwt(request);
//        String username = jwtProvider.getUsernameFromToken(jwt);
//        User user = userService.findByUsername(username).orElseThrow();
//        return new ResponseEntity<>(user, HttpStatus.OK);
//    }
    public ResponseEntity<?> getProfile(HttpServletRequest request) {
        String jwt = jwtTokenFilter.getJwt(request);
        String username = jwtProvider.getUsernameFromToken(jwt);
        User user = userService.findByUsername(username).orElseThrow();
        Member member = profileService.getProfileMember(user);
        return new ResponseEntity<>(member, HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateProfile(HttpServletRequest request, @RequestBody Member member) {
        String jwt = jwtTokenFilter.getJwt(request);
        String username = jwtProvider.getUsernameFromToken(jwt);
        User user = userService.findByUsername(username).orElseThrow();
        Member member1 = profileService.getProfileMember(user);
        member.setUser(user);
        return new ResponseEntity<>(profileService.updateProfile(member, member1.getId()), HttpStatus.OK);
    }

    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(HttpServletRequest request, @RequestBody ChangePasswordForm changePasswordForm) {
        String jwt = jwtTokenFilter.getJwt(request);
        String username = jwtProvider.getUsernameFromToken(jwt);
        User user = userService.findByUsername(username).orElseThrow();

        if(passwordEncoder.matches(changePasswordForm.getOldPassword(), user.getPassword())){
            if(!changePasswordForm.getNewPassword().equals(changePasswordForm.getConfirmPassword())){
                return new ResponseEntity<>(new ResponseMessage("confirm_password_not_match"), HttpStatus.OK);
            }
            user.setPassword(passwordEncoder.encode(changePasswordForm.getNewPassword()));
            userService.save(user);
            return new ResponseEntity<>(new ResponseMessage("change_password_success"), HttpStatus.OK);
        }
        return new ResponseEntity<>(new ResponseMessage("change_password_fail"), HttpStatus.OK);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteProfile(HttpServletRequest request) {
        String jwt = jwtTokenFilter.getJwt(request);
        String username = jwtProvider.getUsernameFromToken(jwt);
        User user = userService.findByUsername(username).orElseThrow();
        profileService.deleteProfile(user.getId());
        return new ResponseEntity<>(new ResponseMessage("deleted"), HttpStatus.OK);
    }
}
