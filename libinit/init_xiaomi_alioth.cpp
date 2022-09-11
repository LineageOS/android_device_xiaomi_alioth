/*
 * Copyright (C) 2021 The LineageOS Project
 *
 * SPDX-License-Identifier: Apache-2.0
 */

#include <libinit_dalvik_heap.h>
#include <libinit_variant.h>

#include "vendor_init.h"

static const variant_info_t aliothin_info = {
    .hwc_value = "INDIA",
    .sku_value = "",

    .brand = "Xiaomi",
    .device = "aliothin",
    .marketname = "Mi 11X",
    .model = "M2012K11AI",
    .build_fingerprint = "Mi/aliothin/aliothin:12/SKQ1.211006.001/V13.0.6.0.SKHINXM:user/release-keys",

    .nfc = false,
};

static const variant_info_t alioth_global_info = {
    .hwc_value = "GLOBAL",
    .sku_value = "",

    .brand = "POCO",
    .device = "alioth",
    .marketname = "POCO F3",
    .model = "M2012K11AG",
    .build_fingerprint = "POCO/alioth_global/alioth:12/SKQ1.211006.001/V13.0.3.0.SKHMIXM:user/release-keys",

    .nfc = true,
};

static const variant_info_t alioth_info = {
    .hwc_value = "",
    .sku_value = "",

    .brand = "Redmi",
    .device = "alioth",
    .marketname = "K40",
    .model = "M2012K11AC",
    .build_fingerprint = "Redmi/alioth/alioth:12/SKQ1.211006.001/V13.0.5.0.SKHCNXM:user/release-keys",

    .nfc = true,
};

static const std::vector<variant_info_t> variants = {
    aliothin_info,
    alioth_global_info,
    alioth_info,
};

void vendor_load_properties() {
    search_variant(variants);
    set_dalvik_heap();
}
