package br.com.ucsal.olimpiadas.service;

import br.com.ucsal.olimpiadas.domain.Questao;

public class QuestaoServiceImpl implements QuestaoService {

    @Override
    public boolean isRespostaCorreta(Questao questao, char marcada) {
        try {
            return questao.getAlternativaCorreta() == Questao.normalizar(marcada);
        } catch (Exception e) {
            return false;
        }
    }
}