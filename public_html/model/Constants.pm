package Constants;

use constant ACTION_GET_ALL => 'getAll';
use constant ACTION_GET => 'get';
use constant ACTION_ADD => 'add';
use constant ACTION_DELETE => 'delete';
use constant ACTION_DELETE_DEVICE => 'deleteDeviceItems';
use constant ACTION_LOGIN => 'login';
use constant ACTION_SYNC => 'sync';
use constant ACTION_RESET => 'reset';
use constant ACTION_UPDATE => 'update';
use constant ACTION_PING => 'ping';

use constant FIELD_ACTION => 'action';
use constant FIELD_DESCRIPTION => 'description';
use constant FIELD_ITEM_ID => 'item_id';
use constant FIELD_PRICE => 'price';
use constant FIELD_USER_ID => 'user_id';
use constant FIELD_UPDATE_USER => 'update_user_id';
use constant FIELD_DEVICE_ID => 'device_id';
use constant FIELD_IS_ADMIN => 'is_admin';
use constant FIELD_IS_ACTIVE => 'is_active';
use constant FIELD_LOGIN => 'login';
use constant FIELD_PIN => 'pin';

use constant RET_RETURN_MESSAGE => 'returnMessage';
use constant RET_RETURN_CODE => 'returnCode';
use constant RET_DATA => 'data';

# Item
use constant ERROR_ITEM_EXISTS => -1;
use constant ERROR_NO_ITEM_FOUND => 1;

# User
use constant ERROR_BAD_PASSWORD => 2;
use constant ERROR_NOT_FOUND => 1;

# Common Error Codes
use constant SUCCESS => 0;
use constant ERROR_MISSING_REQUIRED_FIELDS => -98;
use constant ERROR_NO_ACTION => -99;
use constant ERROR_SIMULATE_DOWN => -97;
use constant ERROR_SIMULATE_BROKEN => -96;
use constant ERROR_SQL_ERROR => -100;

1;
