#!/usr/bin/perl

############################################
#
#   Filename:  ping.pl
#   Author: Geoff Marinski
#
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
use DB;
use JSON;
use Constants;

my $cgi = new CGI;

my $action = $cgi->param(Constants::FIELD_ACTION);
my $deviceID = $cgi->param(Constants::FIELD_DEVICE_ID);

print "Content-type: application/json\n\n";
# application/json

$logger->debug("PING.PL: $action / $deviceID");

my $dao = new DB();
my $a = {};
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

my $json = encode_json $a;
print "$json\n";

$logger->debug("PING RESPONSE: $json");