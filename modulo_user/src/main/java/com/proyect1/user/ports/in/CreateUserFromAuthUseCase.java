package com.proyect1.user.ports.in;

import com.proyect1.user.application.command.CreateUserFromAuthCommand;

public interface CreateUserFromAuthUseCase {

    void execute(CreateUserFromAuthCommand command);
}
