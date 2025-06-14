package com.nexage.app.dto.seller.nativeads.validators;

import static com.nexage.admin.core.enums.nativeads.NativeAssetType.DATA;
import static com.nexage.app.dto.seller.nativeads.asset.type.AssetImageType.ICON;
import static com.nexage.app.dto.seller.nativeads.asset.type.AssetImageType.MAIN;
import static com.nexage.app.dto.seller.nativeads.enums.DataType.CTA_TEXT;
import static com.nexage.app.dto.seller.nativeads.enums.DataType.PRICE;
import static com.nexage.app.dto.seller.nativeads.enums.DataType.SPONSORED;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;

import com.nexage.admin.core.enums.nativeads.NativeAssetRule;
import com.nexage.admin.core.enums.nativeads.NativeAssetType;
import com.nexage.app.dto.seller.nativeads.WebNativePlacementExtensionDTO;
import com.nexage.app.dto.seller.nativeads.asset.NativeAssetSetDTO;
import com.nexage.app.dto.seller.nativeads.asset.type.AssetImageType;
import com.nexage.app.dto.seller.nativeads.asset.type.NativeAssetDTO;
import com.nexage.app.dto.seller.nativeads.asset.type.NativeDataAssetDTO;
import com.nexage.app.dto.seller.nativeads.asset.type.NativeImageAssetDTO;
import com.nexage.app.dto.seller.nativeads.asset.type.NativeTitleAssetDTO;
import com.nexage.app.dto.seller.nativeads.asset.type.NativeVideoAssetDTO;
import com.nexage.app.dto.seller.nativeads.asset.type.VideoProtocols;
import com.nexage.app.dto.seller.nativeads.enums.DataType;
import com.nexage.app.util.ResourceLoader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import javax.validation.ConstraintValidatorContext;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

public class BaseTestWebNativeExtention {

  public static final String VALID_XPATH = "/html/body/div[5]/div[4]";
  public static final String INVALID_XPATH = "word1 word2";

  @Captor protected ArgumentCaptor<String> contextCaptor = ArgumentCaptor.forClass(String.class);

  @Mock protected ConstraintValidatorContext context;

  @Mock protected ConstraintValidatorContext.ConstraintViolationBuilder cBuilder;

  @Mock
  protected ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext
      nBuilder;

  protected Set<NativeAssetDTO> getMandatoryAssetDTOS() {
    Set<NativeAssetDTO> assets = new HashSet<>();

    NativeTitleAssetDTO title = getNativeTitleAssetDTO();
    NativeImageAssetDTO image = getNativeImageAssetDTO(MAIN);
    NativeImageAssetDTO icon = getNativeImageAssetDTO(ICON);
    NativeDataAssetDTO sponsored = getNativeDataAssetDTO(SPONSORED, DATA);

    assets.add(title);
    assets.add(image);
    assets.add(icon);
    assets.add(sponsored);
    return assets;
  }

  protected Set<NativeAssetDTO> getOptionalAssetDTOS() {
    Set<NativeAssetDTO> assets = new HashSet<>();
    NativeDataAssetDTO dataCta = getNativeDataAssetDTO(CTA_TEXT, DATA);
    NativeDataAssetDTO price = getNativeDataAssetDTO(PRICE, DATA);

    assets.add(dataCta);
    assets.add(price);
    return assets;
  }

  protected NativeDataAssetDTO getNativeDataAssetDTO(DataType type, NativeAssetType dataType) {
    NativeDataAssetDTO dataCta = new NativeDataAssetDTO();
    dataCta.getData().setType(type);
    dataCta.setType(dataType);
    dataCta.setKey(getDataKey(type));
    return dataCta;
  }

  private String getDataKey(DataType type) {
    String placeholder = StringUtils.EMPTY;
    switch (type) {
      case SPONSORED:
        placeholder = "sponsoredData";
        break;
      case RATING:
        placeholder = "ratingData";
        break;
      case LIKES:
        placeholder = "likesData";
        break;
      case DOWNLOADS:
        placeholder = "downloadsData";
        break;
      case PRICE:
        placeholder = "priceData";
        break;
      case SALE_PRICE:
        placeholder = "salePriceData";
        break;
      case PHONE:
        placeholder = "phoneData";
        break;
      case ADDRESS:
        placeholder = "addressData";
        break;
      case DESCRIPTION2:
        placeholder = "descriptionData";
        break;
      case DESCRIPTION:
        placeholder = "descriptionData";
        break;
      case DISPLAY_URL:
        placeholder = "displayUrlData";
        break;
      case CTA_TEXT:
        placeholder = "ctaData";
        break;
    }

    return placeholder;
  }

  protected NativeImageAssetDTO getNativeImageAssetDTO(AssetImageType image_type) {
    NativeImageAssetDTO image = new NativeImageAssetDTO();
    image.getImage().setType(image_type);
    image.setType(NativeAssetType.IMAGE);
    image.setKey(image_type == MAIN ? "mainImage" : "iconImage");
    return image;
  }

  protected NativeTitleAssetDTO getNativeTitleAssetDTO() {
    NativeTitleAssetDTO title = new NativeTitleAssetDTO();
    title.setType(NativeAssetType.TITLE);
    title.setKey("title");
    title.getTitle().setMaxLength(12);
    return title;
  }

  protected NativeVideoAssetDTO getNativeVideoAssetDTO() {
    NativeVideoAssetDTO video = new NativeVideoAssetDTO();
    video.setKey("video");
    video.setType(NativeAssetType.VIDEO);
    video.getVideo().setMinDuration(10);
    video.getVideo().setMaxDuration(10);
    video.getVideo().setProtocols(Set.of(VideoProtocols.VAST_1_0));
    return video;
  }

  protected HashSet<NativeAssetSetDTO> getNativeAssetSetDTOS(
      Set<NativeAssetDTO> mandatoryAssets, Set<NativeAssetDTO> optionalAssets) {
    HashSet<NativeAssetSetDTO> assetSets = new HashSet<>();

    NativeAssetSetDTO mandatorySet = new NativeAssetSetDTO();
    mandatorySet.setRule(NativeAssetRule.REQ_ALL);
    mandatorySet.setAssets(mandatoryAssets);

    NativeAssetSetDTO optionalSet = new NativeAssetSetDTO();
    optionalSet.setRule(NativeAssetRule.REQ_NONE);
    optionalSet.setAssets(optionalAssets);

    assetSets.add(mandatorySet);
    assetSets.add(optionalSet);
    return assetSets;
  }

  protected WebNativePlacementExtensionDTO getWebNativePlacementExtensionDTO(
      Set<NativeAssetSetDTO> assetSets) throws IOException {
    WebNativePlacementExtensionDTO nativePlacementExtension = new WebNativePlacementExtensionDTO();

    String encodedHtmlTemplate = getEncodedHtml();
    nativePlacementExtension.setRenderingTemplate(encodedHtmlTemplate);
    nativePlacementExtension.setAdXPath(VALID_XPATH);
    nativePlacementExtension.setAssetSets(assetSets);
    nativePlacementExtension.setAssetTemplate("template name 1");
    return nativePlacementExtension;
  }

  protected String getEncodedHtml() throws IOException {
    String htmlTemplate = getHtmlTemplate();
    return StringEscapeUtils.unescapeJava(htmlTemplate);
  }

  protected String getHtmlTemplate() throws IOException {
    return IOUtils.toString(
        ResourceLoader.getResourceAsStream(
            "/data/nativeplacement/validate/template/testHtmlTemplate.vm"),
        UTF_8);
  }

  protected void initContextMocks() {
    lenient().when(cBuilder.addPropertyNode(anyString())).thenReturn(nBuilder);
    lenient().when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(cBuilder);
  }

  protected WebNativePlacementExtensionDTO prepareTest() throws IOException {
    Set<NativeAssetDTO> mandatoryAssets = getMandatoryAssetDTOS();
    Set<NativeAssetDTO> optionalAssetDTOS = getOptionalAssetDTOS();
    HashSet<NativeAssetSetDTO> assetSets =
        getNativeAssetSetDTOS(mandatoryAssets, optionalAssetDTOS);

    return getWebNativePlacementExtensionDTO(assetSets);
  }
}
