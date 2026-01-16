package org.proyect1;

import jakarta.annotation.PostConstruct;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MyMessagingApplication {

    @ConfigProperty(name = "mp.jwt.verify.publickey.location")
    String pubKeyLocation;

    @PostConstruct
    void init() {
        System.out.println("🔑 Public key location: " + pubKeyLocation);
        try (var is = Thread.currentThread().getContextClassLoader().getResourceAsStream("publicKey.pem")) {
            System.out.println("📄 Public key found? " + (is != null));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
