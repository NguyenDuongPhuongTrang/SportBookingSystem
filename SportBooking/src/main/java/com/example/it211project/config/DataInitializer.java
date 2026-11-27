package com.example.it211project.config;

import com.example.it211project.enums.Role;
import com.example.it211project.model.entity.Court;
import com.example.it211project.model.entity.CourtImage;
import com.example.it211project.model.entity.User;
import com.example.it211project.repository.CourtImageRepository;
import com.example.it211project.repository.CourtRepository;
import com.example.it211project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CourtRepository courtRepository;
    private final CourtImageRepository courtImageRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Seed Users
        if (userRepository.count() == 0) {
            log.info("Bắt đầu khởi tạo tài khoản mẫu...");

            User admin = User.builder()
                    .username("admin")
                    .email("admin@sports.com")
                    .password(passwordEncoder.encode("admin123"))
                    .role(Role.ROLE_ADMIN)
                    .active(true)
                    .build();

            User manager = User.builder()
                    .username("manager")
                    .email("manager@sports.com")
                    .password(passwordEncoder.encode("manager123"))
                    .role(Role.ROLE_MANAGER)
                    .active(true)
                    .build();

            User customer = User.builder()
                    .username("customer")
                    .email("customer@sports.com")
                    .password(passwordEncoder.encode("customer123"))
                    .role(Role.ROLE_CUSTOMER)
                    .active(true)
                    .build();

            userRepository.save(admin);
            userRepository.save(manager);
            userRepository.save(customer);

            log.info("Khởi tạo tài khoản mẫu thành công!");
        }

        // Seed Courts
        if (courtRepository.count() == 0) {
            log.info("Bắt đầu khởi tạo sân mẫu...");

            List<Court> courts = new ArrayList<>();

            courts.add(Court.builder()
                    .courtName("Sân Tennis Premium A")
                    .location("Khu đô thị Sala, Quận 2, TP. HCM")
                    .price(150000.0)
                    .build());

            courts.add(Court.builder()
                    .courtName("Sân Tennis Sky B")
                    .location("Phú Mỹ Hưng, Quận 7, TP. HCM")
                    .price(180000.0)
                    .build());

            courts.add(Court.builder()
                    .courtName("Sân Cầu Lông Arena 1")
                    .location("Phường 25, Quận Bình Thạnh, TP. HCM")
                    .price(70000.0)
                    .build());

            courts.add(Court.builder()
                    .courtName("Sân Cầu Lông Arena 2")
                    .location("Phường 25, Quận Bình Thạnh, TP. HCM")
                    .price(70000.0)
                    .build());

            courtRepository.saveAll(courts);

            // Seed Images for Courts
            String tennisUrl1 = "https://images.unsplash.com/photo-1595435934249-5df7ed86e1c0?auto=format&fit=crop&w=800&q=80";
            String tennisUrl2 = "https://images.unsplash.com/photo-1622279457486-62dce4a4953f?auto=format&fit=crop&w=800&q=80";
            String badmintonUrl1 = "https://images.unsplash.com/photo-1626224583764-f87db24ac4ea?auto=format&fit=crop&w=800&q=80";
            String badmintonUrl2 = "https://images.unsplash.com/photo-1517649763962-0c623066013b?auto=format&fit=crop&w=800&q=80";

            courtImageRepository.save(CourtImage.builder().imageUrl(tennisUrl1).court(courts.get(0)).build());
            courtImageRepository.save(CourtImage.builder().imageUrl(tennisUrl2).court(courts.get(1)).build());
            courtImageRepository.save(CourtImage.builder().imageUrl(badmintonUrl1).court(courts.get(2)).build());
            courtImageRepository.save(CourtImage.builder().imageUrl(badmintonUrl2).court(courts.get(3)).build());

            log.info("Khởi tạo sân mẫu thành công!");
        }
    }
}
