#!/usr/bin/perl

############################################
#
#   Filename:  cart.pl
#   Accepts HTTP parameters as input and returns JSON for the
#   Cart storage (the 'cart' table).
#
#   See the Constants package in the model directory for the literal
#   string values.
#
#   Following the pattern for every POS service, an "action" is required,
#   passed in as parameter, FIELD_ACTION
#
#   Supports the following actions (required fields):
#   ACTION_ADD      (FIELD_CART_JSON,FIELD_DEVICE_ID)
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
use CartDAO;
use Constants;

my $cgi = new CGI;

# The action
my $action = $cgi->param(Constants::FIELD_ACTION);
# The fields
my $deviceID = $cgi->param(Constants::FIELD_DEVICE_ID);
my $cartJSON = $cgi->param(Constants::FIELD_CART_JSON);

# Output the content type for the HTTP response
print "Content-type: application/json\n\n";

$logger->debug("CART.PL: $action");
$logger->debug("ID: $deviceID");

# The ItemDAO for access to the 'cart' table
my $dao = new CartDAO();
# Return structure
my $a = {};
# Action drives what DAO method to use
if (Constants::ACTION_ADD eq $action) {
    if (defined($cartJSON) && defined($deviceID)) {
        my $cart = decode_json $cartJSON;
        $a = $dao->add($cartJSON,$deviceID);
    } else {
        $a->{'returnMessage'} = "Cart data is required.";
        $a->{'returnCode'} = Constants::ERROR_MISSING_REQUIRED_FIELDS;
    }
} else {
    $a->{Constants::RET_RETURN_MESSAGE} = "No action parameter given.";
    $a->{Constants::RET_RETURN_CODE} = Constants::ERROR_NO_ACTION;
}

# Encode and return the JSON response based on the data
my $json = encode_json $a;
print "$json\n";

$logger->debug("CART RESPONSE: $json");
