package com.flowable.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.flowable.entity.CampaignEntity;
import com.flowable.repositories.CampaignRepository;

@Service
public class CampaignService {

  @Autowired
  private CampaignRepository campaignRepository;

  public CampaignEntity saveCampaign(CampaignEntity campaignEntity){
    return campaignRepository.save(campaignEntity);
  }
  
}
