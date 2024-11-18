package com.chen1335.ultimateEnchantment.mixinsAPI;

import com.chen1335.ultimateEnchantment.common.AttributeTypeInfo;

public interface IAttributeExtension {

    void ue$SetSentiment(AttributeTypeInfo.Sentiment ue$sentiment);

    AttributeTypeInfo.Sentiment ue$getSentiment();
}
