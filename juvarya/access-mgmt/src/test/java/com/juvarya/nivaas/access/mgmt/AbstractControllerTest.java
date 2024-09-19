package com.juvarya.nivaas.access.mgmt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.juvarya.nivaas.access.mgmt.azure.service.AwsBlobService;
import com.juvarya.nivaas.access.mgmt.controllers.AuthController;
import com.juvarya.nivaas.access.mgmt.controllers.RoleController;
import com.juvarya.nivaas.access.mgmt.controllers.UserEndpoint;
import com.juvarya.nivaas.access.mgmt.controllers.UserOTPEndpoint;
import com.juvarya.nivaas.access.mgmt.dto.UserOTPDTO;
import com.juvarya.nivaas.access.mgmt.model.UserOTPModel;
import com.juvarya.nivaas.access.mgmt.repository.UserRepository;
import com.juvarya.nivaas.access.mgmt.services.MediaService;
import com.juvarya.nivaas.access.mgmt.services.RoleService;
import com.juvarya.nivaas.access.mgmt.services.UserOTPService;
import com.juvarya.nivaas.access.mgmt.services.UserService;
import com.juvarya.nivaas.access.mgmt.services.impl.UserDetailsServiceImpl;
import com.juvarya.nivaas.utils.converter.AbstractConverter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

@SpringBootTest(classes = AccessManagementApp.class)
@AutoConfigureMockMvc
@SpringJUnitConfig
public abstract class AbstractControllerTest {

    @Mock
    protected UserOTPService userOTPService;
    
    @Mock
    protected AwsBlobService awsBlobService;

    @Mock
    protected MediaService mediaService;

    @Mock
    protected UserRepository userRepository;

    @Mock
    protected UserService userService;

    @Mock
    protected RoleService roleService;

    @Mock
    protected AbstractConverter<UserOTPDTO, UserOTPModel> userOtpConverter;

    @Mock
    protected UserDetailsServiceImpl userDetailsService;

    @InjectMocks
    protected AuthController authController;

    @InjectMocks
    protected UserEndpoint userEndpoint;

    @InjectMocks
    protected RoleController roleController;

    @InjectMocks
    protected UserOTPEndpoint userOTPEndpoint;

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;
    
    @BeforeEach
    protected void init() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(authController, userEndpoint, roleController, userOTPEndpoint)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setViewResolvers((viewName, locale) -> new MappingJackson2JsonView())
                .build();
    }

    @AfterEach
    void releaseMocks() {
        // No need to close mocks individually since we're using MockitoExtension
    }
}