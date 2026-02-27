package com.itau.srv.gerenciamento.clientes.validator;

import com.itau.common.library.exception.ConflitoException;
import com.itau.srv.gerenciamento.clientes.model.Cliente;
import com.itau.srv.gerenciamento.clientes.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class ClienteValidator {

    private final ClienteRepository clienteRepository;

    public void validarCpf(Cliente cliente) {
        if (existeUsuarioComMesmoCpf(cliente)) {
            log.error("Cliente já cadastrado com cpf: {}", cliente.getCpf());
            throw new ConflitoException("CLIENTE_CPF_DUPLICADO");
        }
    }

    private boolean existeUsuarioComMesmoCpf(Cliente cliente) {
        log.info("Iniciando processo de validação para o CPF {}", cliente.getCpf());

        Optional<Cliente> clienteEncontrado = clienteRepository.findByCpfAndAtivo(cliente.getCpf());

        if (cliente.getId() == null) {
            return clienteEncontrado.isPresent();
        }

        return clienteEncontrado.isPresent() && !cliente.getId().equals(clienteEncontrado.get().getId());
    }
}
