#bin/bash

cd ../../../

pwd

SDK_PATH="custom/base/compatiblesdk/build/intermediates/bundles/default/classes.jar"
DATA_PATH="custom/base/data/build/intermediates/bundles/default/classes.jar"
BUSINESS="custom/base/modelprocessor/build/intermediates/bundles/default/classes.jar"

SDK_NAME="compatiblesdk.jar"
DATA_NAME="data.jar"
BUSINESS_NAME="modelprocessor.jar"

router_plugin="custom/base/router/plugin"
router_path="custom/base/router"
dex_path="custom/base/router/dex"


rm -rf $router_plugin/*

cp $SDK_PATH $router_plugin/$SDK_NAME
cp $DATA_PATH $router_plugin/$DATA_NAME
cp $BUSINESS $router_plugin/$BUSINESS_NAME

if [ -f $router_plugin/$SDK_NAME ]; then
	echo "create sdk.dex ."
    $router_path/dx --dex --output $dex_path/sdk.dex $router_plugin/$SDK_NAME
fi

if [  -f $router_plugin/$DATA_NAME ]; then
	echo "create data.dex"
	$router_path/dx --dex --output $dex_path/data.dex $router_plugin/$DATA_NAME
fi

if [  -f $router_plugin/$BUSINESS_NAME ]; then
	echo "create business.dex"
	$router_path/dx --dex --output $dex_path/business.dex $router_plugin/$BUSINESS_NAME
fi
