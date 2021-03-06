package com.fatec.scc.servico;

import javax.persistence.PersistenceException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import com.fatec.scc.model.Cliente;
import com.fatec.scc.model.ClienteRepository;
import com.fatec.scc.model.Endereco;

@Service
public class ClienteServicoI implements ClienteServico {
	Logger logger = LogManager.getLogger(ClienteServicoI.class);
	@Autowired
	private ClienteRepository repository;

	public Iterable<Cliente> findAll() {
		return repository.findAll();
	}

	public Cliente findByCpf(String cpf) {
		return repository.findByCpf(cpf);
	}

	public void deleteById(Long id) {
		repository.deleteById(id);
		logger.info(">>>>>> 2. comando exclusao executado para o id => " + id);
	}

	public Cliente findById(Long id) {
		return repository.findById(id).get();
	}
	/**
	 * valida as informações do cliente e salva
	 */
	public ModelAndView saveOrUpdate(Cliente cliente) {
		ModelAndView modelAndView = new ModelAndView("consultarCliente");
		try {
			String endereco = obtemEndereco(cliente.getCep());
			if (endereco != "") {
				cliente.setEndereco(endereco);
				repository.save(cliente);
				logger.info(">>>>>> 4. comando save executado  ");
				modelAndView.addObject("clientes", repository.findAll());
			}

		} catch (Exception e) {
			modelAndView.setViewName("cadastrarCliente");
			if (e.getMessage().contains("could not execute statement")) {
				modelAndView.addObject("message", "Dados invalidos - cliente já cadastrado.");
				logger.info(">>>>>> 5. cliente ja cadastrado ==> " + e.getMessage());
			} else {
				modelAndView.addObject("message", "Erro não esperado - contate o administrador");
				logger.error(">>>>>> 5. erro nao esperado ==> " + e.getMessage());
			}
		}
		return modelAndView;
	}

	public String obtemEndereco(String cep) {
		RestTemplate template = new RestTemplate();
		String url = "https://viacep.com.br/ws/{cep}/json/";
		Endereco endereco = template.getForObject(url, Endereco.class, cep);
		logger.info(">>>>>> 3. obtem endereco ==> " + endereco.toString());
		return endereco.getLogradouro();
	}

}
