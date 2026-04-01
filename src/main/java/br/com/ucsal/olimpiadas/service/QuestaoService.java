package br.com.ucsal.olimpiadas.service;

import br.com.ucsal.olimpiadas.domain.Questao;

public interface QuestaoService {
    boolean isRespostaCorreta(Questao questao, char marcada);
}