#
# Copyright (C) 2021 The LineageOS Project
#
# SPDX-License-Identifier: Apache-2.0
#

# Inherit from sm8250-common
include device/xiaomi/sm8250-common/BoardConfigCommon.mk

DEVICE_PATH := device/xiaomi/alioth

BUILD_BROKEN_DUP_RULES := true

# A/B
BOARD_USES_RECOVERY_AS_BOOT := true
BOARD_BUILD_SYSTEM_ROOT_IMAGE := false
AB_OTA_UPDATER := true

AB_OTA_PARTITIONS += \
    boot \
    dtbo \
    odm \
    product \
    system \
    system_ext \
    vbmeta \
    vbmeta_system \
    vendor \
    vendor_boot

# Board
TARGET_BOARD_INFO_FILE := $(DEVICE_PATH)/board-info.txt

# Display
TARGET_SCREEN_DENSITY := 420

# Init
TARGET_INIT_VENDOR_LIB := //$(DEVICE_PATH):init_xiaomi_alioth
TARGET_RECOVERY_DEVICE_MODULES := init_xiaomi_alioth

# Kernel
BOARD_MKBOOTIMG_ARGS += --header_version 3
TARGET_KERNEL_CONFIG := vendor/alioth_defconfig

# OTA assert
TARGET_OTA_ASSERT_DEVICE := alioth,aliothin

# Partitions
BOARD_BOOTIMAGE_PARTITION_SIZE := 201326592
BOARD_USERDATAIMAGE_PARTITION_SIZE := 114135379968
BOARD_VENDOR_BOOTIMAGE_PARTITION_SIZE := 100663296
BOARD_PRODUCTIMAGE_PARTITION_RESERVED_SIZE := 268435456
BOARD_SYSTEMIMAGE_PARTITION_RESERVED_SIZE := 1073741824
BOARD_SYSTEM_EXTIMAGE_PARTITION_RESERVED_SIZE := 268435456
BOARD_QTI_DYNAMIC_PARTITIONS_SIZE := 4559208448 # ( BOARD_SUPER_PARTITION_SIZE / 2 - 4MB )

# Properties
TARGET_VENDOR_PROP += $(DEVICE_PATH)/vendor.prop

# Recovery
TARGET_RECOVERY_FSTAB := $(DEVICE_PATH)/rootdir/etc/fstab.qcom

# Inherit from the proprietary version
include vendor/xiaomi/alioth/BoardConfigVendor.mk
