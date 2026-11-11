package com.agrilink.cooperative.service;

import com.agrilink.cooperative.dto.request.CreateCooperativeRequest;
import com.agrilink.cooperative.dto.response.CooperativeResponse;
import com.agrilink.cooperative.dto.response.MembershipResponse;

import java.util.List;

public interface CooperativeService {

    CooperativeResponse createCooperative(CreateCooperativeRequest request);

    List<CooperativeResponse> getAllCooperatives();

    CooperativeResponse getCooperativeById(Long id);

    CooperativeResponse updateCooperative(Long id, CreateCooperativeRequest request);

    void deleteCooperative(Long id);

    List<MembershipResponse> getMembersByCooperativeId(Long cooperativeId);
}
