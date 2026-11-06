package com.quiz.religiousquiz.modules.quiz.repository.jpa;

import com.quiz.religiousquiz.modules.quiz.repository.entity.QuizJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizRepository extends JpaRepository<QuizJpaEntity, String> {
    List<QuizJpaEntity> findByStatusOrderByCriadoEmDesc(String status);
    List<QuizJpaEntity> findAllByOrderByCriadoEmDesc();
}

