package edu.sjsu.cmpe275.Repository;

import edu.sjsu.cmpe275.Domain.Survey;
import edu.sjsu.cmpe275.Domain.SurveyResult.SurveyResult;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Primary
@Repository("surveyResultRepository")
public interface SurveyResultRepository extends CrudRepository<SurveyResult, Long> {
    @Modifying
    @Transactional
    @Query(value="delete from SurveyResult q where q.owner = ?1")
    void deleteByOwner(Survey survey_id);
}
