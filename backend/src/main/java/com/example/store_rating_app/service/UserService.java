// package com.example.store_rating_app.service;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.stereotype.Service;
// import com.example.store_rating_app.entity.Store;
// import com.example.store_rating_app.entity.User;
// import com.example.store_rating_app.repository.StoreRepository;
// import com.example.store_rating_app.repository.UserRepository;
// import java.util.List;
// @Service
// public class UserService {
//     @Autowired
//     private StoreRepository storeRepo;
//     @Autowired
//     private UserRepository userRepo;
//     @Autowired
//     private PasswordEncoder passwordEncoder;
//     // Get all stores
//     public List<Store> getAllStores() {
//         return storeRepo.findAll();
//     }
//     // Search by name or address
//     public List<Store> searchStores(String keyword) {
//         return storeRepo.searchByNameOrAddress(keyword);
//     }
//     // Update password
//     public String updatePassword(String email, String oldPass, String newPass) {
//         User user = userRepo.findByEmail(email)
//                 .orElseThrow(() -> new RuntimeException("User not found"));
//         if (!passwordEncoder.matches(oldPass, user.getPassword())) {
//             throw new RuntimeException("Old password is incorrect");
//         }
//         user.setPassword(passwordEncoder.encode(newPass));
//         userRepo.save(user);
//         return "Password updated successfully";
//     }
// }
package com.example.store_rating_app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.store_rating_app.entity.User;
import com.example.store_rating_app.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Update password using old password + new password
    public String updatePassword(Long userId, String oldPass, String newPass) {

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(oldPass, user.getPassword())) {
            throw new RuntimeException("Old password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPass));
        userRepo.save(user);

        return "Password updated successfully";
    }
}
