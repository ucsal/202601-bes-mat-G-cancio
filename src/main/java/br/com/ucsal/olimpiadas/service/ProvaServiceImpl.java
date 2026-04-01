package br.com.ucsal.olimpiadas.service;

import br.com.ucsal.olimpiadas.domain.Questao;
import br.com.ucsal.olimpiadas.repository.Repository;

import java.util.List;

public class ProvaServiceImpl implements ProvaService {

    private final Repository<Questao> questaoRepository;

    public ProvaServiceImpl(Repository<Questao> questaoRepository) {
        this.questaoRepository = questaoRepository;
    }

    @Override
    public List<Questao> buscarQuestoesDaProva(long provaId) {
        return questaoRepository.listarTodos().stream()
                .filter(q -> q.getProvaId() == provaId)
                .toList();
    }
}