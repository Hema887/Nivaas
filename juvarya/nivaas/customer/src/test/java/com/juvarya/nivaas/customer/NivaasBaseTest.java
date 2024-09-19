package com.juvarya.nivaas.customer;

import com.juvarya.nivaas.commonservice.dto.LoggedInUser;
import com.juvarya.nivaas.commonservice.dto.UserDTO;
import com.juvarya.nivaas.commonservice.enums.ERole;
import com.juvarya.nivaas.commonservice.user.UserDetailsImpl;
import com.juvarya.nivaas.customer.client.AccessMgmtClient;
import com.juvarya.nivaas.customer.firebase.listeners.NotificationPublisher;
import com.juvarya.nivaas.customer.model.ApartmentUserRoleModel;
import com.juvarya.nivaas.customer.model.NivaasApartmentModel;
import com.juvarya.nivaas.customer.populator.OnboardingRequestPopulator;
import com.juvarya.nivaas.customer.proxy.AccessMgmtClientProxy;
import com.juvarya.nivaas.customer.repository.ApartmentAndFlatRelatedUsersModelRepository;
import com.juvarya.nivaas.customer.repository.ApartmentUserRoleRepository;
import com.juvarya.nivaas.customer.repository.NivaasApartmentRepository;
import com.juvarya.nivaas.customer.repository.NivaasFlatRepository;
import com.juvarya.nivaas.customer.repository.OnboardingRequestRepository;
import com.juvarya.nivaas.customer.service.NivaasApartmentService;
import com.juvarya.nivaas.customer.service.NivaasFlatService;
import com.juvarya.nivaas.customer.service.NotificationService;
import com.juvarya.nivaas.customer.service.OnboardingRequestService;
import com.juvarya.nivaas.customer.service.impl.CurrentApartmentServiceImpl;
import com.juvarya.nivaas.customer.service.impl.NivaasApartmentServiceImpl;
import com.juvarya.nivaas.customer.service.impl.ApartmentUserRoleServiceImpl;
import com.juvarya.nivaas.customer.service.impl.NivaasFlatServiceImpl;
import com.juvarya.nivaas.customer.service.impl.OnboardingRequestServiceImpl;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class NivaasBaseTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    protected StaticApplicationContext staticApplicationContext;
    protected NivaasApartmentService apartmentService;
    protected OnboardingRequestService onboardingRequestService;
    protected NivaasFlatService flatService;
    @Autowired
    protected NotificationService notificationService;

    @Autowired
    protected CurrentApartmentServiceImpl currentApartmentService;

    @Autowired
    protected NivaasApartmentRepository apartmentRepository;
    @Autowired
    protected OnboardingRequestRepository onboardingRequestRepository;
    @Autowired
    private OnboardingRequestPopulator onboardingRequestPopulator;
    @MockBean
    protected AccessMgmtClientProxy accessMgmtClientProxy;
    @MockBean
    protected AccessMgmtClient accessMgmtClient;
    @Autowired
    protected ApartmentAndFlatRelatedUsersModelRepository relatedUsersModelRepository;
    @Autowired
    protected NivaasFlatRepository nivaasFlatRepository;
    @Autowired
    protected ApartmentUserRoleRepository apartmentUserRoleRepository;
    @Autowired
    protected ApartmentUserRoleServiceImpl apartmentUserRoleService;
    @MockBean
    protected NotificationPublisher notificationPublisher;
    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    protected UserDTO user = new UserDTO();
    protected LoggedInUser loggedInUser = new LoggedInUser();
    protected UserDTO userDTO = new UserDTO();

    protected void init() {
        setCurrentUser(1L, "1111111111");
        staticApplicationContext = new StaticApplicationContext();
        ConfigurableListableBeanFactory beanFactory = staticApplicationContext.getBeanFactory();
        registerBeans(beanFactory);
    }

    protected void cleanUp() {
        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY FALSE");
        String[] tables = {
                "ONBOARDING_REQUEST", "NIVAAS_FLAT", "NIVAAS_APARTMENT"
        };

        for (String table : tables) {
            jdbcTemplate.execute("DELETE FROM " + table);
        }
        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY TRUE");
    }

    private void registerBeans(ConfigurableListableBeanFactory beanFactory) {
        apartmentService = new NivaasApartmentServiceImpl();
        ReflectionTestUtils.setField(apartmentService, "apartmentRepository", apartmentRepository);
        beanFactory.registerSingleton(NivaasApartmentService.class.getName(), apartmentService);
        flatService = new NivaasFlatServiceImpl();
        ReflectionTestUtils.setField(flatService, "nivaasFlatRepository", nivaasFlatRepository);
        ReflectionTestUtils.setField(flatService, "apartmentRepository", apartmentRepository);
        ReflectionTestUtils.setField(flatService, "apartmentUserRoleRepository", apartmentUserRoleRepository);
        beanFactory.registerSingleton(NivaasFlatService.class.getName(), flatService);
        onboardingRequestService = new OnboardingRequestServiceImpl();
        ReflectionTestUtils.setField(onboardingRequestService, "onboardingRequestRepository", onboardingRequestRepository);
        ReflectionTestUtils.setField(onboardingRequestService, "onboardingRequestPopulator", onboardingRequestPopulator);
        ReflectionTestUtils.setField(onboardingRequestService, "accessMgmtClientProxy", accessMgmtClientProxy);
        ReflectionTestUtils.setField(onboardingRequestService, "accessMgmtClient", accessMgmtClient);
        ReflectionTestUtils.setField(onboardingRequestService, "relatedUsersModelRepository", relatedUsersModelRepository);
        ReflectionTestUtils.setField(onboardingRequestService, "notificationService", notificationService);
        ReflectionTestUtils.setField(onboardingRequestService, "notificationPublisher", notificationPublisher);
        ReflectionTestUtils.setField(onboardingRequestService, "flatService", flatService);
        ReflectionTestUtils.setField(onboardingRequestService, "apartmentService", apartmentService);
        ReflectionTestUtils.setField(onboardingRequestService, "apartmentUserRoleRepository", apartmentUserRoleRepository);
        ReflectionTestUtils.setField(onboardingRequestService, "currentApartmentService", currentApartmentService);
        beanFactory.registerSingleton(OnboardingRequestService.class.getName(), onboardingRequestService);
    }

    protected void setCurrentUser(final Long userId, final String primaryContact) {
        user.setId(userId);
        user.setPrimaryContact(primaryContact);
        loggedInUser.setId(user.getId());
        loggedInUser.setPrimaryContact(user.getPrimaryContact());
        userDTO.setId(user.getId());
        SecurityContextHolder.setContext(securityContext);
        UserDetailsImpl userDetails = new UserDetailsImpl(user.getId(), user.getPrimaryContact(), "", "", null);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        Mockito.when(authentication.getPrincipal()).thenReturn(userDetails);
        Mockito.when(accessMgmtClient.getByPrimaryContact(Mockito.any())).thenReturn(loggedInUser);
        Mockito.when(accessMgmtClient.getUserById(Mockito.any())).thenReturn(userDTO);
    }

    protected NivaasApartmentModel saveTestApartment(final String name) {
        NivaasApartmentModel apartment = new NivaasApartmentModel();
        apartment.setId(2L);
        apartment.setName(name);
        apartment.setCreatedBy(user.getId());
        apartment.setTotalFlats(2);
        return apartmentService.saveApartment(apartment);
    }

    protected ApartmentUserRoleModel markUserAsAdmin(final NivaasApartmentModel nivaasApartmentModel, final Long adminUserId) {
        return apartmentUserRoleService.onBoardApartmentAdminOrHelper(nivaasApartmentModel, adminUserId, ERole.ROLE_APARTMENT_ADMIN.name());
    }
}
