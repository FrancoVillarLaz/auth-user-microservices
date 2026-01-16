package com.proyect1.user.config.domain;


import com.proyect1.user.domain.service.UserDomainService;
import com.proyect1.user.domain.service.UserSubscriptionDomainService;
import com.proyect1.user.ports.out.UserStandardRepositoryPort;
import com.proyect1.user.ports.out.UserSubscriptionRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainServiceConfig {

    //Realmente la arquitectura hexagonal al pie de la letra es un bardo, los proximos
    //servicios los inyecto altoque en applicacion
    @Bean
    public UserDomainService userDomainService(UserStandardRepositoryPort userStandardRepositoryPort) {
        return new UserDomainService(userStandardRepositoryPort);
    }

    // posdata, sigo usandolo
    @Bean
    public UserSubscriptionDomainService userSubscriptionDomainService(
            UserSubscriptionRepositoryPort repo
    ) {
        return new UserSubscriptionDomainService(repo);
    }

}
