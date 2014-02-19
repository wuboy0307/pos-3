package UserDAO;
use DB;
our @ISA = qw(DB);    # inherits from DB

############################################
#
#   Filename:  DAO.pm (package DAO)
#   Author: 
#
############################################

use strict;
use DBI;
use Log::Log4perl;
use Sys::Hostname;
use Data::Dumper;
use String::Util 'trim';

my $logger = Log::Log4perl->get_logger('opuma');

my $THIS_YEAR = 2013;



sub stub {
    my $self = shift;
    
    my $dbh = $self->{'dbh'};
    
    my $sql = "select * from user where login = ''";
    
    my $sth = $dbh->prepare($sql);
    $sth->execute();
    
    my ($ret, $rc, $rm);
    
    if (my $ref = $sth->fetchrow_hashref()) {
    
    }
    $sth->finish();
    
    $ret->{'returnMessage'} = $rm;
    $ret->{'returnCode'} = $rc;
    
    $dbh->disconnect();
    
    return $ret;
}

sub addUser {
    my $self = shift;
    my $login = shift;
    my $pin = shift;
    my $isAdmin = shift;
    
    my $dbh = $self->{'dbh'};
    
    my $sql = "select count(*) as count from user where login = '$login'";
    
    my $sth = $dbh->prepare($sql);
    $sth->execute();
    
    my ($ret, $rc, $rm);
    
    my $count;
    if (my $ref = $sth->fetchrow_hashref()) {
        $count = $ref->{'count'};
    }
    $sth->finish();
    
    if ($count > 0) {
        $rm = "Login already exists: $login";
        $rc = 1;
    } else {
        $sql = "insert into user (login,pin,is_admin) values (?,?,?)";
        $logger->debug("SQL: $sql / $login / $pin / $isAdmin");
        $sth = $dbh->prepare($sql);
        $sth->execute($login,$pin,$isAdmin);
        
        my $ret->{'user_id'} = $dbh->{'mysql_insertid'};
        $ret->{'is_active'} = "Y";
        $ret->{'is_admin'} = $isAdmin;
        $ret->{'login'} = $login;
        $ret->{'pin'} = $pin;
        
        $sth->finish();
        
        $rm = "Success";
        $rc = 0;
    }
    
    $ret->{'returnMessage'} = $rm;
    $ret->{'returnCode'} = $rc;
    
    $dbh->disconnect();
    
    return $ret;
}

sub updateUser {
    my $self = shift;
    my $login = shift;
    my $pin = shift;
    my $isAdmin = shift;
    my $isActive = shift;
    my $id = shift;
    
    $logger->debug("DAO: $id");
    
    $login =~ s/'/''/g;
    $pin =~ s/'/''/g;
    $isAdmin =~ s/'/''/g;
    $isActive =~ s/'/''/g;
    
    
    my $dbh = $self->{'dbh'};
    
    my $sql;
    if (!defined($id)) { 
        $sql = "select user_id from user where login = '$login'";
        $logger->debug("updateUser based on login $login");
    } else {
        $sql = "select user_id from user where user_id = $id";
        $logger->debug("updateUser based on userID $id");
    }
    $logger->debug("SQL: $sql");
    
    my $sth = $dbh->prepare($sql);
    $sth->execute();
    
    my ($ret, $rc, $rm);
    
    my $count;
    if (my $ref = $sth->fetchrow_hashref()) {
        $id = $ref->{'user_id'};
        $sth->finish();
        
        $sql = "update user set login = '$login', pin = '$pin', is_admin = '$isAdmin', is_active = '$isActive' where user_id = $id";
        $logger->debug("SQL: $sql");
        $sth = $dbh->prepare($sql);
        $sth->execute();
        
        $ret->{'user_id'} = $id;
        $ret->{'is_active'} = $isActive;
        $ret->{'is_admin'} = $isAdmin;
        $ret->{'login'} = $login;
        $ret->{'pin'} = $pin;
        
        $sth->finish();
        
        $rc = 0;
        $rm = "Success";
    } else {
        $sth->finish();
        $rc = 2;
        $rm = "No user found for login $login";
    }
    
    $ret->{'returnMessage'} = $rm;
    $ret->{'returnCode'} = $rc;
    
    $dbh->disconnect();
    
    return $ret;
}

sub deleteUser {
    my $self = shift;
    my $login = shift;
    
    my $dbh = $self->{'dbh'};
    
    my $sql = "select user_id from user where login = '$login'";
    
    my $sth = $dbh->prepare($sql);
    $sth->execute();
    
    my ($ret, $rc, $rm);
    
    if (my $ref = $sth->fetchrow_hashref()) {
        my $id = $ref->{'user_id'};
        $sth->finish();
        
        $sql = "delete from user where user_id = $id";
        $sth = $dbh->prepare($sql);
        $sth->execute();
        
        $sth->finish();
        
        $rm = "Success";
        $rc = 0;
    } else {
        $sth->finish();
        
        $rm = "User not found for login $login";
        $rc = 2;
    }
    
    $ret->{'returnMessage'} = $rm;
    $ret->{'returnCode'} = $rc;
    
    $dbh->disconnect();
    
    return $ret;
}

sub login {
	my $self = shift;
	my $login = shift;
	my $password = shift;
	
	my $dbh = $self->{'dbh'};

	my $sql = "select * from user where login = '$login'";
	
	my $sth = $dbh->prepare($sql);
	$sth->execute();

	my ($ret, $rc, $rm);

	if (my $ref = $sth->fetchrow_hashref()) {
		if ($ref->{'pin'} eq $password) {
			$rc = 0;
			$rm = "Success";
		} else {
			$rc = 2;
			$rm = "Password doesn't match";
		}
		$ret = $ref;
		# $ret->{'pin'} = "****";  Do this later??
	} else {
		$rc = 1;
		$rm = "Login not found: $login"; 
	}
	$sth->finish();
	
	$ret->{'returnMessage'} = $rm;
	$ret->{'returnCode'} = $rc;
	
	$dbh->disconnect();
	
	return $ret;
}

sub getUsers() {
    my $self = shift;
    
    my $dbh = $self->{'dbh'};

    my $sql = "select * from user order by login";
    
    my $sth = $dbh->prepare($sql);
    $sth->execute();

    my ($ret, $rc, $rm, $ref, @a, $count);

    $count = 0;
    while ($ref = $sth->fetchrow_hashref()) {
        push @a, $ref;
        $count++;
    }
    $sth->finish();
    
    $rm = "Success: $count";
    $rc = 0;
    
    $ret->{'returnMessage'} = $rm;
    $ret->{'returnCode'} = $rc;
    $ret->{'data'} = \@a;
    
    $dbh->disconnect();
    
    return $ret;
}

