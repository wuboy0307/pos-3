#!/usr/bin/perl

############################################
#
#   Filename:  ping.pl
#   Accepts HTTP parameters as input and returns JSON.  This service
#   is for a POS appliction to check on the connectivity and functionality.
#
#   This can artificially return bad values based on configuration to simulate
#   down conditions.  The following settings from 'system_settings' are looked at.
#   The settings can be both on, but the code will simulate the conditions in the
#   order that is given:
#   simulate_down_nice      Returns a RET_RETURN_MESSAGE and RET_RETURN_CODE
#   simulate_down_broken    Returns nothing..which is what happens when the DAO explodes
#
#
#   See the Constants package in the model directory for the literal
#   string values.
#
#   Following the pattern for every POS service, an "action" is required,
#   passed in as parameter, FIELD_ACTION
#
#   Supports the following actions (required fields):
#   ACTION_PING
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
use DB;
use Constants;

my $cgi = new CGI;

# The action
my $action = $cgi->param(Constants::FIELD_ACTION);
my $deviceID = $cgi->param(Constants::FIELD_DEVICE_ID);

$logger->debug("PING.PL: $action / $deviceID");

# The DAO class for POS
my $dao = new DB();
# Return structure
my $a = {};
# Action selection
if (Constants::ACTION_PING eq $action) {
    $a = $dao->getSettings();
    if (defined $a && $a->{Constants::RET_RETURN_CODE} == 0) {
        if ($a->{Constants::RET_DATA}->{'simulate_down_nice'} eq '1') {
            $a->{Constants::RET_RETURN_MESSAGE} = "Simulating down.";
            $a->{Constants::RET_RETURN_CODE} = Constants::ERROR_SIMULATE_DOWN;
        } elsif ($a->{Constants::RET_DATA}->{'simulate_down_broken'} eq '1') {
            $a = undef;
        }
    } else {
        $a->{Constants::RET_RETURN_MESSAGE} = "Problem with backend connection.";
        $a->{Constants::RET_RETURN_CODE} = Constants::ERROR_SIMULATE_BROKEN;
    }
} else {
    $a->{Constants::RET_RETURN_MESSAGE} = "No action parameter given.";
    $a->{Constants::RET_RETURN_CODE} = Constants::ERROR_NO_ACTION;
}

# Output the content type for the HTTP response
print "Content-type: application/json\n\n";
# Encode and return the JSON response based on the data
my $json = encode_json $a;
print "$json\n";

$logger->debug("PING RESPONSE: $json");