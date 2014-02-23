#!/usr/bin/perl

############################################
#
#   Filename:  item.pl
#   Author: Geoff Marinski
#
#   Serves the AJAX calls for the login service.
#
#   Returns JSON.  So I guess it is AJAJ.
#
############################################

use strict;
use lib qw(/home/Students/g_m108/perllib /home/Students/g_m108/perllib/x86_64-linux-thread-multii ../model);

use Data::Dumper;
use Log::Log4perl qw(:easy);
    Log::Log4perl->easy_init($DEBUG);
Log::Log4perl::init('../cfg/log4perl.conf');

my $logger = Log::Log4perl->get_logger('opuma');

use CGI;
use ItemDAO;
use JSON;
use Constants;

my $cgi = new CGI;

my $action = $cgi->param(Constants::FIELD_ACTION);
my $id = $cgi->param(Constants::FIELD_ITEM_ID);
my $description = $cgi->param(Constants::FIELD_DESCRIPTION);
my $price = $cgi->param(Constants::FIELD_PRICE);
my $deviceID = $cgi->param(Constants::FIELD_DEVICE_ID);
my $updateUser = $cgi->param(Constants::FIELD_UPDATE_USER);

print "Content-type: application/json\n\n";
# application/json

$logger->debug("ITEM.PL: $action");
$logger->debug("ID: $id / $deviceID");

my $dao = new ItemDAO();
my $a = {};
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

my $json = encode_json $a;
print "$json\n";

$logger->debug("ITEM RESPONSE: $json");