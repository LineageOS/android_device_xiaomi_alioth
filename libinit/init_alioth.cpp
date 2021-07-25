/*
 * Copyright (C) 2021 The LineageOS Project
 *
 * SPDX-License-Identifier: Apache-2.0
 */

#include <libinit_kona.h>

static const variant_info_t aliothcn_info = {
    .hwc_value = "CN",
    .sku_value = "",

    .brand = "Redmi",
    .device = "alioth",
    .marketname = "K40",
    .model = "M2012K11AC",
    .build_description = "alioth-user 11 RKQ1.200826.002 V12.5.4.0.RKHCNXM release-keys",
    .build_fingerprint = "Redmi/alioth/alioth:11/RKQ1.200826.002/V12.5.4.0.RKHCNXM:user/release-keys",

    .nfc = true,
};

static const variant_info_t aliothin_info = {
    .hwc_value = "INDIA",
    .sku_value = "",

    .brand = "Xiaomi",
    .device = "aliothin",
    .marketname = "Mi 11X",
    .model = "M2012K11AI",
    .build_description = "aliothin-user 11 RKQ1.200826.002 V12.5.2.0.RKHMIXM release-keys",
    .build_fingerprint = "Mi/aliothin/aliothin:11/RKQ1.200826.002/V12.5.2.0.RKHMIXM:user/release-keys",

    .nfc = false,
};

static const variant_info_t alioth_info = {
    .hwc_value = "GLOBAL",
    .sku_value = "",

    .brand = "POCO",
    .device = "alioth",
    .marketname = "POCO F3",
    .model = "M2012K11AG",
    .build_description ="alioth-user 11 RKQ1.200826.002 V12.5.2.0.RKHMIXM release-keys",
    .build_fingerprint = "POCO/alioth_global/alioth:11/RKQ1.200826.002/V12.5.2.0.RKHMIXM:user/release-keys",

    .nfc = true,
};

static const std::vector<variant_info_t> variants = {
    aliothcn_info,
    aliothin_info,
    alioth_info,
};

void vendor_load_properties() {
    search_variant(variants);
}
