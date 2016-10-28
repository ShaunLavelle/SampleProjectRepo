package uk.co.sample.project.url;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import uk.co.sample.project.domain.Url;

@Service
public class UrlService {

  @Resource
  UrlDao urlDao;

  @Resource
  TransactionTemplate transactionTemplate;

  public List<Url> getAllUrl() {
    return transactionTemplate.execute(status -> urlDao.getAllUrl());
  }
}
