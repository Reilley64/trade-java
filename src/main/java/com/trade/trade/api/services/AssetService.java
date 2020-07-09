package com.trade.trade.api.services;

import com.trade.trade.api.repositories.AssetRepository;
import com.trade.trade.api.repositories.ExchangeRepository;
import com.trade.trade.clients.iexcloud.IEXCloudClient;
import com.trade.trade.clients.iexcloud.objects.IEXAssetProfile;
import com.trade.trade.domain.exceptions.ResourceNotFoundException;
import com.trade.trade.domain.models.Asset;
import com.trade.trade.domain.models.Exchange;
import org.springframework.stereotype.Service;

@Service
public class AssetService {
    private final AssetRepository repository;
    private final ExchangeRepository exchangeRepository;
    private final IEXCloudClient iexCloudClient;

    public AssetService(AssetRepository repository, ExchangeRepository exchangeRepository, IEXCloudClient iexCloudClient) {
        this.repository = repository;
        this.exchangeRepository = exchangeRepository;
        this.iexCloudClient = iexCloudClient;
    }

    public void createAsset(Asset asset) {
        IEXAssetProfile iexAssetProfile = iexCloudClient.getAssetProfile(asset);
        asset.setName(iexAssetProfile.getCompanyName());
        asset.setImage("https://storage.googleapis.com/iex/api/logos/" + asset.getSymbol() + ".png");
        asset.setExchange(exchangeRepository.findByUuid(asset.getExchange().getUuid())
                .orElseThrow(() -> new ResourceNotFoundException(Exchange.class, asset.getExchange().getUuid())));
        asset.setDescription(iexAssetProfile.getDescription());
        asset.setIndustry(iexAssetProfile.getIndustry());
        asset.setSector(iexAssetProfile.getSector());
        asset.setWebsite(iexAssetProfile.getWebsite());
        repository.save(asset);
    }
}
