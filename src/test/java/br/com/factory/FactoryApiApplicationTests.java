package br.com.factory;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import br.com.factory.domain.model.Contato;
import br.com.factory.domain.model.Pessoa;
import br.com.factory.domain.repository.PessoaRepository;
import br.com.factory.util.DataBaseCleaner;
import br.com.factory.util.ResourceUtils;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestPropertySource("/application-test.properties")
public class FactoryApiApplicationTests {
	
	
	private static final int PESSOA_ID_INEXISTENTE = 100;

	
	@LocalServerPort
	private int port;
	
	@Autowired
	private PessoaRepository pessosRepository;
	
	
	@Autowired
	private DataBaseCleaner dataBaseCleaner;
	
	private int quantidePessoaCadastrada;
	private Pessoa p1;
	
	
	private String jsonPessoaCorreto;
	private String jsonPessoaDataFutura;
	private String jsonPessoaSemContato;
	private String jsonPessoaComContatoSemEmail;
	private String jsonPessoaComContatoEmailInvalido;
	private String jsonPessoaComCpfInvalido;
	private String jsonPessoaComDoisContatos;
	private String JsonPessoaSemNomESemEmail;
	
	
	@Before
	public void setup() {
		RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
		RestAssured.port = this.port;
		RestAssured.basePath = "/pessoa";
		
		
		jsonPessoaCorreto = ResourceUtils.getContentFromResource("/json/correto/pessoa.json");
		
		jsonPessoaComDoisContatos = ResourceUtils.getContentFromResource("/json/correto/pessoa_com_dois_contatos.json");

		jsonPessoaDataFutura = ResourceUtils.getContentFromResource("/json/incorreto/pessoa-com-data-futura.json");
		
		jsonPessoaSemContato = ResourceUtils.getContentFromResource("/json/incorreto/pessoa-sem-contato.json");
 		
		jsonPessoaComContatoSemEmail = ResourceUtils.getContentFromResource("/json/incorreto/pessoa-com-contato-sem-email.json"); 
		
		jsonPessoaComContatoEmailInvalido = ResourceUtils.getContentFromResource("/json/incorreto/pessoa-com-contato-email-invalido.json");
		
		jsonPessoaComCpfInvalido = ResourceUtils.getContentFromResource("/json/incorreto/pessoa-com-cpf-invalido.json"); 
		
		JsonPessoaSemNomESemEmail = ResourceUtils.getContentFromResource("/json/incorreto/pessoa-sem-nome-e-sem-email.json"); 
		
		dataBaseCleaner.clearTables();
		preparaDados();
		
	}
	
	@Test
	public void deveRetornarStatus200_QuandoConsultarPessoa() {
		given().
			accept(ContentType.JSON).
		when().
			get().
		then().
			statusCode(HttpStatus.OK.value());
	}
	
	@Test
	public void deveRetornarDoisRegistros_QuandoConsultarPessoa() {		
		given().
			accept(ContentType.JSON).
		when().
			get().
		then().
			body("content", hasSize(quantidePessoaCadastrada));
	}
	
	
	@Test
	public void deveRetornarStatus404_QuandoPessoaInexistente() {
		given()
			.pathParam("id", PESSOA_ID_INEXISTENTE)
			.accept(ContentType.JSON)
		.when()
			.get("/{id}")
		.then()
			.statusCode(HttpStatus.NOT_FOUND.value());
	}
	
	@Test
	public void deveRetornarRespostaEStatusCorreto_QuandoConsultarPessoaExistente() {
		given()
			.pathParam("id", p1.getId())
			.accept(ContentType.JSON)
		.when()
			.get("/{id}")
		.then()
			.statusCode(HttpStatus.OK.value())
			.body("nome", equalTo(p1.getNome()));
	}
	

	@Test
	public void deveRetornar201_QuandoAdicionarUmaPessoa() {
		given().
			body(jsonPessoaCorreto).
			contentType(ContentType.JSON).
			accept(ContentType.JSON).
		when().
			post().
		then().
		  statusCode(HttpStatus.CREATED.value());
			
	}
	
	@Test
	public void deveRetornar204_QuandoDeletarUmaPessa() {
		given()
			.pathParam("id", p1.getId())
			.accept(ContentType.JSON)
		.when()
			.delete("/{id}")
		.then()
			.statusCode(HttpStatus.NO_CONTENT.value());			
	}
	
	@Test
	public void deveRetornar400_QuandoSalvarUmaPessoaComDataFutura() {
		given().
			body(jsonPessoaDataFutura).
			contentType(ContentType.JSON).
			accept(ContentType.JSON)
		.when()
			.post()
		.then()
			.statusCode(HttpStatus.BAD_REQUEST.value())
			.body("campos[0].campo", equalTo("dataNascimento"))
			.body("campos[0].mensagem", equalTo("data deve ser igual ou anterior ao dia de hoje"));
	 
	}
		
	
	@Test
	public void deveRetornar400_QuandoSalvarUmaPessoaSemContato() {
		given().
			body(jsonPessoaSemContato).
			contentType(ContentType.JSON).
			accept(ContentType.JSON)
		.when()
			.post()
		.then()
			.statusCode(HttpStatus.BAD_REQUEST.value())			
			.body("campos[0].campo", equalTo("contatos"))
		    .body("campos[0].mensagem", equalTo("não deve estar vazio"));
	 
	}
	
	@Test
	public void deveRetornar400_QuandoSalvarUmaPessoaComContatoSemEmail() {
		given().
			body(jsonPessoaComContatoSemEmail).
			contentType(ContentType.JSON).
			accept(ContentType.JSON)
		.when()
			.post()
		.then()
			.statusCode(HttpStatus.BAD_REQUEST.value())			
			.body("campos[0].campo", equalTo("contatos[0].email"))
		    .body("campos[0].mensagem", equalTo("não deve estar em branco"));
	
	}
	
	@Test
	public void deveRetornar200_QuandoAtualizarPessoa() {
		p1.setNome("Gabriel");
		given()
			.pathParam("id", p1.getId())
			.body(p1)
			.contentType(ContentType.JSON)			
			.accept(ContentType.JSON)
		.when()
			.put("/{id}")
		.then()
			.statusCode(HttpStatus.OK.value())
			.body("nome", equalTo(p1.getNome()));
	}
	
	@Test
	public void deveRetornar200_QuandoAtualizarContato() {
		p1.getContatos().get(0).setEmail("email123@email.com.br");
		given()
			.pathParam("id", p1.getId())
			.body(p1)
			.contentType(ContentType.JSON)			
			.accept(ContentType.JSON)
		.when()
			.put("/{id}")
		.then()
			.statusCode(HttpStatus.OK.value())
			.body("contatos[0].email", equalTo(p1.getContatos().get(0).getEmail()));
	}
	
	@Test
	public void deveRetornar40_QuandoEmailContatoInvalido() {
		given().
			body(jsonPessoaComContatoEmailInvalido).
			contentType(ContentType.JSON).
			accept(ContentType.JSON)
		.when()
			.post()
		.then()
			.statusCode(HttpStatus.BAD_REQUEST.value())			
			.body("campos[0].campo", equalTo("contatos[0].email"))
			.body("campos[0].mensagem", equalTo("deve ser um e-mail válido"));
	}
	
	@Test
	public void deveRetornar40_QuandoCpfPessoaInvalido() {
		given().
			body(jsonPessoaComCpfInvalido).
			contentType(ContentType.JSON).
			accept(ContentType.JSON)
		.when()
			.post()
		.then()
			.statusCode(HttpStatus.BAD_REQUEST.value())			
			.body("campos[0].campo", equalTo("Cpf"))
			.body("campos[0].mensagem", equalTo("número do registro de contribuinte individual brasileiro (CPF) inválido"));
	}
	
	@Test
	public void deveRetornar201EDoisContatos_QuandoCadastrarPessoa(){
		given().
			body(jsonPessoaComDoisContatos).
			contentType(ContentType.JSON).
			accept(ContentType.JSON).
		when().
			post().
		then().
		  statusCode(HttpStatus.CREATED.value())
		  .body("contatos", hasSize(2));
	}

	
	@Test 
	public void deveRetornar404EMaisDeUmaValidacao_QuandoCadastroPessoaInvalido() {
		given().
			body(JsonPessoaSemNomESemEmail).
			contentType(ContentType.JSON).
			accept(ContentType.JSON)
		.when()
			.post()
		.then()
			.statusCode(HttpStatus.BAD_REQUEST.value())			
			.body("campos", hasSize(2));	
	}
	
	@Test
	public void deveRetornar201ELocationNoHeader_QuandoAdicionarUmaPessoa() {
		given()
			.body(p1)
			.contentType(ContentType.JSON)			
			.accept(ContentType.JSON)
		.when()
			.post()
		.then()
			.statusCode(HttpStatus.CREATED.value())
			.header("Location", equalTo(String.format("http://localhost:%d/pessoa/%d", port, p1.getId())));
			
	}
	
	
	
	private void preparaDados() {
		p1 = new Pessoa();
		Contato c1 = new Contato();
			
		p1.setNome("Andre");
		p1.setDataNascimento(LocalDate.parse("1986-02-12"));
		p1.setCpf("41022589067");		
		
		c1.setEmail("email@email.com");
		c1.setNome("contato");
		c1.setTelefone("9999999999");
		c1.setPessoa(p1);
		
		p1.getContatos().add(c1);
		
		pessosRepository.save(p1);
		
		Pessoa p2 = new Pessoa();
		Contato c2 = new Contato();
			
		p2.setNome("Aline");
		p2.setDataNascimento(LocalDate.parse("1982-02-13"));
		p2.setCpf("39949155037");		
		
		c2.setEmail("aline@email.com");
		c2.setNome("contato");
		c2.setTelefone("9999999999");
		c2.setPessoa(p1);
		
		p2.getContatos().add(c2);
		
		pessosRepository.save(p2);
		
		quantidePessoaCadastrada = (int) pessosRepository.count();
		
		
	}

}
