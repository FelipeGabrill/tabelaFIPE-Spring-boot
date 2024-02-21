package com.tabelafipe.FIPE.principal;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import com.tabelafipe.FIPE.model.Dados;
import com.tabelafipe.FIPE.model.Modelos;
import com.tabelafipe.FIPE.model.Veiculo;
import com.tabelafipe.FIPE.service.ConsumoAPI;
import com.tabelafipe.FIPE.service.ConverteDados;


public class Principal {

	public Scanner sc = new Scanner(System.in);
	private ConsumoAPI consumo = new ConsumoAPI();
	private ConverteDados conversor = new ConverteDados(); 
	
	private final String ENDERECO = "https://parallelum.com.br/fipe/api/v1/";

	public void exibeMenu() {
		
		var menu = """
				****OPÇÕES***
				Carro
				Moto
				Caminhao
				*************
				""";
		System.out.println(menu);
		System.out.print("Digite uma das opcoes para consulta: ");
		var opcaoVeiculo = sc.nextLine().toLowerCase();
		String URL;
		
		if (opcaoVeiculo.toLowerCase().contains("carr")) {
			URL = ENDERECO + "carros/marcas";
		} else if (opcaoVeiculo.toLowerCase().contains("mot")) {
			URL = ENDERECO + "motos/marcas";
		} else {
			URL = ENDERECO + "caminhoes/marcas";
		}
		
		var json = consumo.obterDados(URL);
		System.out.println(json);
		
		var codMarca = conversor.obterLista(json, Dados.class);
		
		codMarca.stream()
			.sorted(Comparator.comparing (Dados::codigo))
			.forEach(System.out::println);

		System.out.print("\nInforme o código da marca para consulta:");
        var codigoMarca = sc.nextLine();

        URL = URL + "/" + codigoMarca + "/modelos";
        json = consumo.obterDados(URL);
        var modeloLista = conversor.obterDados(json, Modelos.class);
        System.out.println(modeloLista);        
        
        System.out.println("\nModelos dessa marca: ");
        modeloLista.modelos().stream()
			.sorted(Comparator.comparing (Dados::codigo))
			.forEach(System.out::println);
	
        System.out.print("\nDigite um trecho do nome do carro a ser buscado: ");
        var nomeVeiculo = sc.nextLine();
        
        List<Dados> modelosFiltrados = modeloLista.modelos().stream()
        		.filter(m -> m.nome().toLowerCase().contains(nomeVeiculo.toLowerCase()))
        		.collect(Collectors.toList());
        
        System.out.println("\nModelos filtrados: ");
        modelosFiltrados.forEach(System.out::println);
        
        System.out.print("\nDigite o codigo do modelo: ");
        var codigoModelo = sc.nextLine();
        
        URL = URL + "/" + codigoModelo + "/anos";
        json = consumo.obterDados(URL);
        List<Dados> anos = conversor.obterLista(json, Dados.class);
        
        List<Veiculo> veiculos = new ArrayList<>();
        
        for (int i = 0; i < anos.size(); i++) {
			var enderecoAnos = URL + "/" + anos.get(i).codigo();
			json = consumo.obterDados(enderecoAnos);
			Veiculo veiculo = conversor.obterDados(json, Veiculo.class);
			veiculos.add(veiculo);
		}
        
        System.out.println("\nTodos os veiculos filtrados com avaliacoes por ano: ");
        veiculos.forEach(System.out::println);
        
	}
}
