package br.com.ucsal.olimpiadas.service;

import br.com.ucsal.olimpiadas.domain.Questao;

import java.util.List;

public interface ProvaService {
    List<Questao> buscarQuestoesDaProva(long provaId);
}