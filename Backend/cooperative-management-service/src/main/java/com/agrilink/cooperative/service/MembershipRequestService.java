package com.agrilink.cooperative.service;

import com.agrilink.cooperative.dto.request.MembershipRequestDto;
import com.agrilink.cooperative.dto.response.MembershipRequestResponse;

import java.util.List;

public interface MembershipRequestService {

    MembershipRequestResponse submitRequest(Long cooperativeId, MembershipRequestDto request);

    MembershipRequestResponse acceptRequest(Long requestId);

    MembershipRequestResponse rejectRequest(Long requestId);

    List<MembershipRequestResponse> getPendingRequestsByCooperative(Long cooperativeId);

    List<MembershipRequestResponse> getAllRequestsByCooperative(Long cooperativeId);
}
