package com.flowable.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "campaign_data")
public class CampaignEntity { 
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "campaign_name", unique = true)
  private String campaign_name;

  private String email_id;

  private String mobile_number;

  public Long id() {
    return id;
  }

  public void id(Long id) {
    this.id = id;
  }

  public String getCampaign_name() {
    return campaign_name;
  }

  public void setCampaign_name(String campaign_name) {
    this.campaign_name = campaign_name;
  }

  public String getEmail_id() {
    return email_id;
  }

  public void setEmail_id(String email_id) {
    this.email_id = email_id;
  }

  public String getMobile_number() {
    return mobile_number;
  }

  public void setMobile_number(String mobile_number) {
    this.mobile_number = mobile_number;
  }
  
}
