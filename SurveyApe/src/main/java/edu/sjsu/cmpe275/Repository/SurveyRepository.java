package edu.sjsu.cmpe275.Repository;

import edu.sjsu.cmpe275.Domain.Survey;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Transactional(Transactional.TxType.REQUIRED)
@Repository("surveyRepository")
@Primary
public interface SurveyRepository extends CrudRepository<Survey, Long>{
    Survey findBySurveyToken(String surveyToken);
    @Modifying
    @Transactional
    @Query(value="delete from Survey s where s.surveyToken = ?1")
    void deleteBySurveyToken(String surveyToken);
    @Transactional
    void deleteById(Long id);
}
