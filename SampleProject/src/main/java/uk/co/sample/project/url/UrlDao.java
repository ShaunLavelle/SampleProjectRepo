package uk.co.sample.project.url;

import java.util.List;

import org.springframework.stereotype.Component;

import uk.co.sample.project.domain.Url;
import uk.co.sample.project.generic.AbstractBasicDao;

@Component
public class UrlDao extends AbstractBasicDao {

  @SuppressWarnings("unchecked")
  public List<Url> getAllUrl() {
    return getCurrentSession().createQuery("from Url").list();
  }
}
