# Terminology

## Domain Classes 

### Seller 

- _Legacy name_: `Publisher`.

### Placement 

- _Legacy name_: `Position`.

### DSP 

- _Legacy name_: `Buyer`.
- _Description_: Parent level in the hierarchy. It is the umbrella for a company, so The Trade Desk would be the DSP level, 
  and then within it, it can have buyers, buyer groups, bidder configs and ad networks.

### Bidder Configurations

- _Alternative name_: `BidderConfig`.
- _Description_: This is where we send inventory to dsp, bidder endpoint where they receive request. 
  This is the connection through which they can buy inventory. Dsps for SSP classic will have an endpoint in the East or APAC 
  (SSP classic added the West endpoint recently with the introduction of a new data center). `BRXD` traffic does have all 4 endpoints.

### Buyers

- _Alternative name_: `BuyerSeat`.
- _Description_: Buyers are seats through which an agency or advertiser can buy (could be a seat for an advertiser, agency, etc). 
  Buyers could bid through different bidders since some span several regions.

### Buyer groups

- _Alternative name_: `BuyerGroup`.
- _Description_: Where buyers can get rolled up into. This is what we currently use for billing.
