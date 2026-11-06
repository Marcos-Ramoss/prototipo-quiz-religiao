package com.quiz.religiousquiz.modules.quiz.repository.jpa;

import com.quiz.religiousquiz.modules.quiz.repository.entity.ParticipacaoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParticipacaoRepository extends JpaRepository<ParticipacaoJpaEntity, String> {
}

