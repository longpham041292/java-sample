package asia.cmg.f8.profile.domain.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;

import asia.cmg.f8.profile.database.entity.UserDistrictLocationEntity;
import asia.cmg.f8.profile.domain.repository.UserDistrictLocationRepository;
import asia.cmg.f8.profile.dto.LocationDistanceDTO;
import asia.cmg.f8.profile.dto.SuggestedTrainersDTO;

@Service
public class UserDistrictLocationService {
	private static final Logger LOG = LoggerFactory.getLogger(UserDistrictLocationService.class);
	private static final Gson GSON = new Gson();
			
	@Autowired
	UserDistrictLocationRepository userLocationRepo;

	public List<UserDistrictLocationEntity> getByUserUuid(final String userUuid) throws Exception {
		try {
			List<UserDistrictLocationEntity> resultList = userLocationRepo.findByUserUuid(userUuid);
			return resultList;
		} catch (Exception e) {
			throw e;
		}
	}
	
	public UserDistrictLocationEntity save(UserDistrictLocationEntity entity) throws Exception {
		try {
			LOG.info("Insert UserDistrictLocationEntity object to DB {}", GSON.toJson(entity));
			return userLocationRepo.saveAndFlush(entity);
		} catch (Exception e) {
			throw e;
		}
	}
	
	public void save(final List<UserDistrictLocationEntity> entities, final String userUuid) throws Exception {
		try {
			userLocationRepo.deleteByUserUuid(userUuid);
			LOG.info("Deleted district location of user [{}]", userUuid);
			
			userLocationRepo.save(entities);
			LOG.info("Inserted new district locations of user [{}]", userUuid);
		} catch (Exception e) {
			throw e;
		}
	}
	
	public List<SuggestedTrainersDTO> findTopNearestTrainers(final String userUuid, double latitude, double longitude, int limit) {
		try {
			final int LIMIT_RADIUS = Integer.MAX_VALUE;
			List<Object[]> nearestTrainers = userLocationRepo.
											findNearestTrainersByLocation(latitude, longitude, LIMIT_RADIUS, limit);
			List<SuggestedTrainersDTO> suggestedTrainers = nearestTrainers
					.stream()
					.map(trainer -> {
						return new SuggestedTrainersDTO((String)trainer[0], 
														(String)trainer[1], 
														(String)trainer[2], 
														(String)trainer[3],
														(String)trainer[4],
														(int)trainer[5]);
					}).collect(Collectors.toList());
					
			return suggestedTrainers;
		} catch (Exception e) {
			return Collections.emptyList();
		}
	}
	
	public List<LocationDistanceDTO> findTopNearestDistricts(final String userUuid, final Double maxRadiusInKm, int limit) {
		try {
			List<UserDistrictLocationEntity> districts = userLocationRepo.getByUserUuid(userUuid);
			List<LocationDistanceDTO> result = new ArrayList<LocationDistanceDTO>();
			Map<String, String> mapOfUserUuids = new HashMap<String, String>();
			int count = 0;
			if(districts.isEmpty()) {
				return Collections.emptyList();
			} else {
				for (UserDistrictLocationEntity districtLocation : districts) {
					if(limit == 0) {
						break;
					}
					count = 0;
					List<LocationDistanceDTO> nearestDistricts = userLocationRepo.
							getTopNearestDistrictLocation(districtLocation.getLatitude(), districtLocation.getLongtitude(), 30, limit);
					
					for (LocationDistanceDTO nearestDistrict : nearestDistricts) {
						if(mapOfUserUuids.get(nearestDistrict.getUserUuid()) == null) {
							mapOfUserUuids.put(nearestDistrict.getUserUuid(), nearestDistrict.getUserUuid());
							result.add(nearestDistrict);
							count ++;
						}
					}
					
					limit = limit - count;
				}
				
				// Sorting the result list by distance_in_km
				Collections.sort(result);
				
				return result;
			}
		} catch (Exception e) {
			LOG.error("[findTopNearestDistricts] error detail: {}", e.getMessage());
			return Collections.emptyList();
		}
	}
	
	public List<LocationDistanceDTO> getTopNearestDistrictByGeoLocation(final Double latitude, final Double longtitude, final Double maxRadiusInKm, final int limit) {
		try {
			return userLocationRepo.getTopNearestDistrictLocation(latitude, longtitude, 30, limit);
		} catch (Exception e) {
			LOG.error("[getTopNearestDistrictLocation] error detail: {}", e.getMessage());
			return Collections.emptyList();
		}
	}
}
