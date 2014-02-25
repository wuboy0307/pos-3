#!/usr/bin/perl

############################################
#
#   Filename:  item.pl
#   Accepts HTTP parameters as input and returns JSON for the
#   Item storage (the 'item' table).
#
#   See the Constants package in the model directory for the literal
#   string values.
#
#   Following the pattern for every POS service, an "action" is required,
#   passed in as parameter, FIELD_ACTION
#
#   Supports the following actions (required fields):
#   ACTION_GET_ALL
#   ACTION_GET      (FIELD_ITEM_ID)
#   ACTION_ADD      (FIELD_ITEM_ID,FIELD_DESCRIPTION,FIELD_PRICE,FIELD_DEVICE_ID,FIELD_UPDATE_USER)
#   ACTION_UPDATE   (FIELD_ITEM_ID,FIELD_DESCRIPTION,FIELD_PRICE,FIELD_DEVICE_ID,FIELD_UPDATE_USER)
#   ACTION_DELETE   (FIELD_ITEM_ID)
#   ACTION_RESET    (FIELD_DEVICE_ID)
#   ACTION_SYNC     (FIELD_DEVICE_ID)
#
############################################

use strict;
# Libray path designed to run on the Texas State CS student servers
use lib qw(/home/Students/g_m108/perllib /home/Students/g_m108/perllib/x86_64-linux-thread-multii ../model);

# Log4Perl and Data Dumper for logging and debugging
use Data::Dumper;
use Log::Log4perl qw(:easy);
    Log::Log4perl->easy_init($DEBUG);
Log::Log4perl::init('../cfg/log4perl.conf');

my $logger = Log::Log4perl->get_logger('opuma');

# CGI and JSON handling
use CGI;
use JSON;

# POS database access and field constants
use ItemDAO;
use Constants;

my $cgi = new CGI;

# The action
my $action = $cgi->param(Constants::FIELD_ACTION);
# The fields
my $id = $cgi->param(Constants::FIELD_ITEM_ID);
my $description = $cgi->param(Constants::FIELD_DESCRIPTION);
my $price = $cgi->param(Constants::FIELD_PRICE);
my $deviceID = $cgi->param(Constants::FIELD_DEVICE_ID);
my $updateUser = $cgi->param(Constants::FIELD_UPDATE_USER);

# Output the content type for the HTTP response
print "Content-type: application/json\n\n";

$logger->debug("ITEM.PL: $action");
$logger->debug("ID: $id / $deviceID");

# The ItemDAO for access to the 'item' table
my $dao = new ItemDAO();
# Return structure
my $a = {};
# Action drives what DAO method to use
if (Constants::ACTION_GET_ALL eq $action) {
    $a = $dao->getItems();
} elsif (Constants::ACTION_GET eq $action) {
    $a = $dao->get($id);
} elsif (Constants::ACTION_ADD eq $action) {
    $a = $dao->add($id,$description,$price,$deviceID,$updateUser);
} elsif (Constants::ACTION_UPDATE eq $action) {
    $a = $dao->update($id,$description,$price,$deviceID,$updateUser);
} elsif (Constants::ACTION_DELETE eq $action) {
    $a = $dao->delete($id);
} elsif (Constants::ACTION_RESET eq $action) {
    $a = $dao->reset($deviceID);
} elsif (Constants::ACTION_SYNC eq $action) {
    $a = $dao->sync($deviceID);
} else {
    $a->{Constants::RET_RETURN_MESSAGE} = "No action parameter given.";
    $a->{Constants::RET_RETURN_CODE} = Constants::ERROR_NO_ACTION;
}

# Encode and return the JSON response based on the data
my $json = encode_json $a;
print "$json\n";

$logger->debug("ITEM RESPONSE: $json");