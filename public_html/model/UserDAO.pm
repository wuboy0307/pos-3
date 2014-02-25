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
use Constants;

my $logger = Log::Log4perl->get_logger('opuma');

sub addUser {
    my $self = shift;
    my $login = shift;
    my $pin = shift;
    my $isAdmin = shift;
    
    my $dbh = $self->{'dbh'};
    
    my $sql = "select count(*) as count from user where login = ?";
    
    my ($ret, $rc, $rm);
    
    my $sth = $dbh->prepare($sql);
    $sth->execute($login);
    if ($sth->err() > 0) {
        $ret->{Constants::RET_RETURN_MESSAGE} = "SQL ERROR: $sql /" . $sth->err() . "/" . $sth->errstr();
        $ret->{Constants::RET_RETURN_CODE} = Constants::ERROR_SQL_ERROR;
        $logger->error("SQL ERROR: $sql /" . $sth->err() . "/" . $sth->errstr());
        return $ret;
    }
    
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
        
        $sth = $dbh->prepare($sql);
        $sth->execute($login,$pin,$isAdmin);
        if ($sth->err() > 0) {
            $ret->{Constants::RET_RETURN_MESSAGE} = "SQL ERROR: $sql /" . $sth->err() . "/" . $sth->errstr();
            $ret->{Constants::RET_RETURN_CODE} = Constants::ERROR_SQL_ERROR;
            $logger->error("SQL ERROR: $sql /" . $sth->err() . "/" . $sth->errstr());
            return $ret;
        }   
        
        $ret->{Constants::FIELD_USER_ID} = $dbh->{'mysql_insertid'};
        $ret->{Constants::FIELD_IS_ACTIVE} = "Y";
        $ret->{Constants::FIELD_IS_ADMIN} = $isAdmin;
        $ret->{Constants::FIELD_LOGIN} = $login;
        $ret->{Constants::FIELD_PIN} = $pin;
        
        $sth->finish();
        
        $rm = "Success";
        $rc = 0;
    }
    
    $ret->{Constants::RET_RETURN_MESSAGE} = $rm;
    $ret->{Constants::RET_RETURN_CODE} = $rc;
    
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
    
    $id =~ s/'/''/g;
    $login =~ s/'/''/g;
    $pin =~ s/'/''/g;
    $isAdmin =~ s/'/''/g;
    $isActive =~ s/'/''/g;
    
    my ($ret, $rc, $rm);
    
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
    if ($sth->err() > 0) {
        $ret->{Constants::RET_RETURN_MESSAGE} = "SQL ERROR: $sql /" . $sth->err() . "/" . $sth->errstr();
        $ret->{Constants::RET_RETURN_CODE} = Constants::ERROR_SQL_ERROR;
        $logger->error("SQL ERROR: $sql /" . $sth->err() . "/" . $sth->errstr());
        return $ret;
    }
    
    my $count;
    if (my $ref = $sth->fetchrow_hashref()) {
        $id = $ref->{'user_id'};
        $sth->finish();
        
        $sql = "update user set login = '$login', pin = '$pin', is_admin = '$isAdmin', is_active = '$isActive' where user_id = $id";
        $logger->debug("SQL: $sql");
        $sth = $dbh->prepare($sql);
        $sth->execute();
        if ($sth->err() > 0) {
            $ret->{Constants::RET_RETURN_MESSAGE} = "SQL ERROR: $sql /" . $sth->err() . "/" . $sth->errstr();
            $ret->{Constants::RET_RETURN_CODE} = Constants::ERROR_SQL_ERROR;
            $logger->error("SQL ERROR: $sql /" . $sth->err() . "/" . $sth->errstr());
            return $ret;
        }
        
        
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
    
    $ret->{Constants::RET_RETURN_MESSAGE} = $rm;
    $ret->{Constants::RET_RETURN_CODE} = $rc;
    
    $dbh->disconnect();
    
    return $ret;
}

sub deleteUser {
    my $self = shift;
    my $login = shift;
    
    my $dbh = $self->{'dbh'};
    
    my $sql = "select user_id from user where login = '$login'";
    
    my ($ret, $rc, $rm);
    
    my $sth = $dbh->prepare($sql);
    $sth->execute();
    if ($sth->err() > 0) {
        $ret->{Constants::RET_RETURN_MESSAGE} = "SQL ERROR: $sql /" . $sth->err() . "/" . $sth->errstr();
        $ret->{Constants::RET_RETURN_CODE} = Constants::ERROR_SQL_ERROR;
        $logger->error("SQL ERROR: $sql /" . $sth->err() . "/" . $sth->errstr());
        return $ret;
    }
    
    if (my $ref = $sth->fetchrow_hashref()) {
        my $id = $ref->{'user_id'};
        $sth->finish();
        
        $sql = "delete from user where user_id = $id";
        $sth = $dbh->prepare($sql);
        $sth->execute();
        if ($sth->err() > 0) {
            $ret->{Constants::RET_RETURN_MESSAGE} = "SQL ERROR: $sql /" . $sth->err() . "/" . $sth->errstr();
            $ret->{Constants::RET_RETURN_CODE} = Constants::ERROR_SQL_ERROR;
            $logger->error("SQL ERROR: $sql /" . $sth->err() . "/" . $sth->errstr());
            return $ret;
        }
        
        $sth->finish();
        
        $rm = "Success";
        $rc = 0;
    } else {
        $sth->finish();
        
        $rm = "User not found for login $login";
        $rc = 2;
    }
    
    $ret->{Constants::RET_RETURN_MESSAGE} = $rm;
    $ret->{Constants::RET_RETURN_CODE} = $rc;
    
    $dbh->disconnect();
    
    return $ret;
}

sub login {
	my $self = shift;
	my $login = shift;
	my $password = shift;
	
	my ($ret, $rc, $rm);
	   
	my $dbh = $self->{'dbh'};

	my $sql = "select * from user where login = ?";

	my $sth = $dbh->prepare($sql);
	$sth->execute($login);
    if ($sth->err() > 0) {
        $ret->{Constants::RET_RETURN_MESSAGE} = "SQL ERROR: $sql /" . $sth->err() . "/" . $sth->errstr();
        $ret->{Constants::RET_RETURN_CODE} = Constants::ERROR_SQL_ERROR;
        $logger->error("SQL ERROR: $sql /" . $sth->err() . "/" . $sth->errstr());
        return $ret;
    }

	if (my $ref = $sth->fetchrow_hashref()) {
		if ($ref->{'pin'} eq $password) {
			$rc = Constants::SUCCESS;
			$rm = "Success";
		} else {
			$rc = Constants::ERROR_BAD_PASSWORD;
			$rm = "Password doesn't match";
		}
		$ret = $ref;
		# $ret->{'pin'} = "****";  Do this later??
	} else {
		$rc = Constants::ERROR_NOT_FOUND;
		$rm = "Login not found: $login"; 
	}
	$sth->finish();
	
	$ret->{Constants::RET_RETURN_MESSAGE} = $rm;
	$ret->{Constants::RET_RETURN_CODE} = $rc;
	
	$dbh->disconnect();
	
	return $ret;
}

sub getUsers() {
    my $self = shift;
    
    my $dbh = $self->{'dbh'};

    my $sql = "select * from user order by login";
    
    my ($ret, $rc, $rm, $ref, @a, $count);
    
    my $sth = $dbh->prepare($sql);
    $sth->execute();
    if ($sth->err() > 0) {
        $ret->{Constants::RET_RETURN_MESSAGE} = "SQL ERROR: $sql /" . $sth->err() . "/" . $sth->errstr();
        $ret->{Constants::RET_RETURN_CODE} = Constants::ERROR_SQL_ERROR;
        $logger->error("SQL ERROR: $sql /" . $sth->err() . "/" . $sth->errstr());
        return $ret;
    }

    $count = 0;
    while ($ref = $sth->fetchrow_hashref()) {
        push @a, $ref;
        $count++;
    }
    $sth->finish();
    
    $rm = "Success: $count";
    $rc = 0;
    
    $ret->{Constants::RET_RETURN_MESSAGE} = $rm;
    $ret->{Constants::RET_RETURN_CODE} = $rc;
    $ret->{Constants::RET_DATA} = \@a;
    
    $dbh->disconnect();
    
    return $ret;
}

1;