package com.juvarya.nivaas.access.mgmt;

import com.juvarya.nivaas.access.mgmt.model.Role;
import com.juvarya.nivaas.access.mgmt.model.User;
import com.juvarya.nivaas.access.mgmt.repository.CustomerLastLoginRepository;
import com.juvarya.nivaas.access.mgmt.repository.MediaRepository;
import com.juvarya.nivaas.access.mgmt.repository.RoleRepository;
import com.juvarya.nivaas.access.mgmt.repository.UserOTPRepository;
import com.juvarya.nivaas.access.mgmt.repository.UserRepository;
import com.juvarya.nivaas.access.mgmt.services.CustomerLastLoginService;
import com.juvarya.nivaas.access.mgmt.services.RoleService;
import com.juvarya.nivaas.access.mgmt.services.UserOTPService;
import com.juvarya.nivaas.access.mgmt.services.UserService;
import com.juvarya.nivaas.access.mgmt.services.impl.CustomerLastLoginServiceImpl;
import com.juvarya.nivaas.access.mgmt.services.impl.RoleServiceImpl;
import com.juvarya.nivaas.access.mgmt.services.impl.UserOTPServiceImpl;
import com.juvarya.nivaas.access.mgmt.services.impl.UserServiceImpl;
import com.juvarya.nivaas.commonservice.enums.ERole;
import com.juvarya.nivaas.access.mgmt.services.MediaService;
import com.juvarya.nivaas.access.mgmt.services.impl.MediaServiceImpl;
import com.juvarya.nivaas.auth.JwtUtils;
import com.juvarya.nivaas.access.mgmt.services.CustomerState;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class AccessBaseTest {

    protected StaticApplicationContext staticApplicationContext;
    protected UserService userService;
    protected RoleService roleService;
    protected MediaService mediaService;
    protected UserOTPService userOTPService;
    protected CustomerLastLoginService customerLastLoginService;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected RoleRepository roleRepository;

    @Autowired
    protected MediaRepository mediaRepository;

    @Autowired
    protected UserOTPRepository userOTPRepository;

    @Autowired
    protected CustomerLastLoginRepository customerLastLoginRepository;

    @Mock
    protected JwtUtils jwtUtils;

    @Autowired
    protected CustomerState customerState;

    protected void init() {
        addTestUser();
        staticApplicationContext = new StaticApplicationContext();
        ConfigurableListableBeanFactory beanFactory = staticApplicationContext.getBeanFactory();

        userService = new UserServiceImpl();  // Instantiate UserServiceImpl directly
        ReflectionTestUtils.setField(userService, "userRepository", userRepository);
        beanFactory.registerSingleton(UserService.class.getName(), userService);

        roleService = new RoleServiceImpl();  // Instantiate RoleServiceImpl directly
        ReflectionTestUtils.setField(roleService, "roleRepository", roleRepository);
        beanFactory.registerSingleton(RoleService.class.getName(), roleService);

        mediaService = new MediaServiceImpl();  // Instantiate MediaServiceImpl directly
        ReflectionTestUtils.setField(mediaService, "mediaRepository", mediaRepository);
        beanFactory.registerSingleton(MediaService.class.getName(), mediaService);

        userOTPService = new UserOTPServiceImpl();  // Instantiate UserOTPServiceImpl directly
        ReflectionTestUtils.setField(userOTPService, "userOTPRepository", userOTPRepository);
        beanFactory.registerSingleton(UserOTPService.class.getName(), userOTPService);

        // Initialize CustomerLastLoginService and use injected repository
        customerLastLoginService = new CustomerLastLoginServiceImpl();
        ReflectionTestUtils.setField(customerLastLoginService, "customerLastLoginRepository", customerLastLoginRepository);
        beanFactory.registerSingleton(CustomerLastLoginService.class.getName(), customerLastLoginService);

        // Inject JwtUtils and CustomerState into AccessBaseTest
        ReflectionTestUtils.setField(customerState, "jwtUtils", jwtUtils);
    }

    protected User buildUser() {
        User user = new User();
        user.setId(1L);
        user.setPrimaryContact("1111111111");
        user.setEmail("test@example.com");
        return user;
    }

    protected Role buildRole(ERole eRole) {
        Role role = new Role();
        role.setName(eRole);
        return role;
    }

    protected void addTestUser() {
        // Example method to set up a test user
        User user = new User();
        user.setId(1L);
        user.setPrimaryContact("1111111111");
        //userRepository.save(user); // Uncomment if you need to save the user
    }
}
